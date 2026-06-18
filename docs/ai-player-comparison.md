# AI Player Comparison

## What Problem This Solves

The existing analytics API can compare players when a client already knows both database IDs. This feature adds a natural-language entry point for questions such as:

```text
Compare Kohli and Smith
```

The backend extracts the names, matches them against persisted players, loads the existing verified profile statistics, and asks an AI provider to explain those values. The AI is used only for wording. It is not the source of any cricket statistic.

This is a grounded summarization feature, not an autonomous agent. It has no tools, memory, planning loop, vector database, Kafka workflow, or retrieval store.

## Endpoint

```text
POST /api/ai/compare-players
Content-Type: application/json
```

Request:

```json
{
  "question": "Compare Kohli and Smith"
}
```

A successful response contains the matched player identities, the existing backend comparison DTO, an explanation, and known data limitations:

```json
{
  "question": "Compare Kohli and Smith",
  "matchedPlayers": {
    "player1": { "playerId": 53, "playerName": "V Kohli" },
    "player2": { "playerId": 49, "playerName": "SPD Smith" }
  },
  "stats": {
    "player1": {
      "playerId": 53,
      "playerName": "V Kohli",
      "matches": 121,
      "runs": 9230,
      "ballsFaced": 16655,
      "strikeRate": 55.42
    },
    "player2": {
      "playerId": 49,
      "playerName": "SPD Smith",
      "matches": 118,
      "runs": 10763,
      "ballsFaced": 20031,
      "strikeRate": 53.73
    }
  },
  "summary": "A grounded explanation based only on these values.",
  "limitations": [
    "ballsFaced currently includes wides due to known schema limitation"
  ]
}
```

## Request And Response Flow

1. A frontend or API client sends a question to `AiComparisonController`.
2. `PlayerNameExtractor` extracts two search terms from supported comparison wording.
3. `AiPlayerComparisonService` calls `AnalyticsService.searchPlayers` for each term.
4. The service either selects one unambiguous player per term or returns candidate lists.
5. For two distinct matched players, the service calls `AnalyticsService.comparePlayers`.
6. The existing analytics service produces both `PlayerProfileDto` values, including strike rates.
7. `AiComparisonProvider` receives the original question and the verified `PlayerComparisonDto`.
8. `OpenAiComparisonProvider` serializes that DTO into the prompt and calls the configured Responses API.
9. If the provider succeeds, its grounded explanation is returned.
10. If the provider is unavailable, times out, rejects the request, or returns no text, the service returns the same stats with a deterministic fallback summary.

The database is queried before the AI provider is called. The provider never receives repository objects, SQL results, or an instruction to derive new statistics.

## New Classes And Records

### Controller

`AiComparisonController`

Exposes `POST /api/ai/compare-players`. Its `comparePlayers` method passes the request question to the service. OpenAPI annotations describe successful, ambiguous, and invalid requests.

### Services

`AiPlayerComparisonService`

Coordinates extraction, player search, ambiguity handling, statistics retrieval, AI generation, and fallback generation.

Important methods:

- `comparePlayers`: Runs the complete use case and creates the API response.
- `selectUniqueMatch`: Prefers one case-insensitive full-name match. If there is no full-name match, it accepts a search result only when the search returned exactly one candidate.
- `requireCandidates`: Produces a clear `400 Bad Request` when a search term finds no players.
- `generateSummary`: Calls the provider and switches to fallback text when provider generation fails or returns blank text.
- `buildFallbackSummary`: Formats only the four existing profile fields and adds simple higher/equal comparisons.
- `compareRuns` and `compareStrikeRates`: Compare existing values without deriving new cricket statistics.

`PlayerNameExtractor`

Keeps natural-language parsing separate from orchestration. `extractPlayerNames` recognizes these forms:

- `Compare Kohli and Smith`
- `Compare Kohli vs Smith`
- `Kohli versus Smith`
- `How do Virat Kohli and Steve Smith compare?`

Its `cleanName` helper removes surrounding quotes and trailing punctuation. Unsupported or incomplete questions produce a `400` response rather than sending a vague prompt to the AI provider.

### AI Provider Boundary

`AiComparisonProvider`

Defines the provider-neutral `generateComparisonSummary` method. The service depends on this interface, so a future provider can replace the current implementation without changing player matching or analytics logic.

`OpenAiComparisonProvider`

Implements the interface using the OpenAI Responses API. It configures bounded connection and read timeouts, adds bearer authentication at request time, builds the grounded prompt, and extracts returned text.

Important methods:

- `generateComparisonSummary`: Validates configuration, sends the request, and converts HTTP or serialization failures into `AiProviderException`.
- `buildInput`: Serializes only the verified `PlayerComparisonDto` and places it beside the user's original question.
- `extractOutputText`: Reads the top-level response text or the nested `output_text` content format and rejects empty provider responses.
- `stripTrailingSlash`: Builds a stable `/responses` URL from a configurable provider base URL.

`AiProviderProperties`

Binds `app.ai.*` configuration from `application.yaml`. It exists so credentials, model choice, endpoint URL, timeouts, and output limits are external configuration rather than hardcoded operational settings.

### API DTOs

`AiPlayerComparisonRequest`

Contains the natural-language `question` supplied by the client.

`AiPlayerComparisonResponse`

Contains the echoed question and either a completed comparison or ambiguity candidates. Empty fields are omitted from JSON, so an ambiguity response does not pretend that stats or an AI summary exist.

`AiMatchedPlayersDto`

Holds the two canonical `PlayerSearchDto` identities selected by the matching process.

`AiPlayerMatchCandidatesDto`

Connects one extracted search term to its candidate players when the backend cannot select a unique match safely.

### Exceptions

`InvalidAiComparisonException`

Represents client-correctable problems such as an unsupported question, no matching player, or both names resolving to the same player. `GlobalExceptionHandler.handleInvalidAiComparison` converts it into the project's standard `400 Bad Request` body.

`AiProviderException`

Represents provider configuration, HTTP, timeout, serialization, and empty-output failures. It is caught inside the AI comparison service because provider failure should not discard verified cricket statistics.

## How Player Matching Works

The extractor returns two strings in question order. Each string is passed to the existing `AnalyticsService.searchPlayers` method, which uses the existing case-insensitive partial-name database search.

For each result list:

1. One case-insensitive full-name match is selected.
2. Otherwise, one result is selected only when it is the sole candidate.
3. More than one possible result is treated as ambiguous.
4. No results produce `400 Bad Request` with the unmatched search term.

After matching, the service checks player IDs. If both names resolve to the same ID, it returns `400 Bad Request` and does not call the analytics comparison or AI provider.

## How Ambiguity Is Handled

The backend never chooses the first partial search result. When a term has multiple candidates and no unique full-name match, the response contains candidates instead of stats:

```json
{
  "question": "Compare Smith and Kohli",
  "summary": "Player matching is ambiguous. Use more specific player names.",
  "candidates": [
    {
      "query": "Smith",
      "candidates": [
        { "playerId": 49, "playerName": "SPD Smith" },
        { "playerId": 71, "playerName": "JA Smith" }
      ]
    }
  ]
}
```

This is returned with HTTP `200` because the question was understandable and the client can continue by submitting a more specific name. No AI call occurs for an ambiguous request.

## How Statistics Are Fetched

Once both player IDs are known, `AiPlayerComparisonService` calls the existing `AnalyticsService.comparePlayers` method. That method calls the existing player-profile logic for each ID and returns `PlayerComparisonDto`.

The AI feature does not query repositories directly and does not duplicate strike-rate calculations. This preserves one authoritative implementation for profile statistics.

## Prompt Construction And Grounding

The provider sends two separate pieces to the model:

- High-priority instructions that restrict the model to supplied fields and prohibit unsupported claims.
- An input containing the original question and serialized `PlayerComparisonDto` JSON.

The instructions permit discussion only of:

- `matches`
- `runs`
- `ballsFaced`
- `strikeRate`

They explicitly prohibit invented averages, centuries, rankings, venue records, opposition records, and career claims. The prompt also tells the model to treat the user's question as context, not as authority to override grounding rules.

## Why AI Does Not Calculate Statistics

Language models are useful for explaining structured data but are not a reliable source of exact arithmetic or cricket scoring rules. Allowing the provider to calculate statistics would create two competing business-logic implementations and could produce inconsistent API responses.

All numeric values therefore originate in PostgreSQL queries and `AnalyticsService`. The AI receives completed values and converts them into readable prose. It may identify which supplied value is higher, but it must not derive or invent a new statistic.

## Fallback Summary

Provider generation is intentionally non-critical. `AiPlayerComparisonService` catches `AiProviderException`, logs a warning without exposing credentials, and builds deterministic text from the same profile DTOs.

The fallback lists each player's matches, runs, balls faced, and strike rate. It then states which existing run total and strike-rate value is higher, or that they are equal. It does not calculate averages or introduce outside cricket knowledge.

This means missing credentials, provider downtime, rate limits, malformed provider output, and configured timeouts do not remove the verified `stats` portion of the response.

## Environment Variables

`AI_API_KEY`

Required only for live provider-generated summaries. It must be supplied through the runtime environment or secret manager. When omitted, the endpoint uses the deterministic fallback summary.

`AI_MODEL`

Optional model ID. Default: `gpt-5.4-mini`.

`AI_BASE_URL`

Optional provider base URL. Default: `https://api.openai.com/v1`. This is mainly useful for compatible gateways or future infrastructure changes.

`AI_CONNECT_TIMEOUT`

Optional connection timeout using Spring duration syntax. Default: `3s`.

`AI_READ_TIMEOUT`

Optional response timeout using Spring duration syntax. Default: `15s`.

`AI_MAX_OUTPUT_TOKENS`

Optional maximum provider output tokens. Default: `300`.

Never commit an API key to `application.yaml`, `.env`, source code, tests, or documentation. Use a local environment variable and a managed secret in Cloud Run.

## Manual Testing

Start the application with PostgreSQL configured. To test the live provider path:

```bash
export AI_API_KEY=your-provider-key
export AI_MODEL=gpt-5.4-mini
./mvnw spring-boot:run
```

Send a successful request:

```bash
curl -X POST http://localhost:8080/api/ai/compare-players \
  -H 'Content-Type: application/json' \
  -d '{"question":"Compare Kohli and Smith"}'
```

Test fallback behavior by starting without `AI_API_KEY`. The response should still contain `matchedPlayers`, `stats`, `summary`, and `limitations`.

Test ambiguity with a surname that returns multiple database players:

```bash
curl -X POST http://localhost:8080/api/ai/compare-players \
  -H 'Content-Type: application/json' \
  -d '{"question":"Compare Smith and Kohli"}'
```

Test validation:

```bash
curl -X POST http://localhost:8080/api/ai/compare-players \
  -H 'Content-Type: application/json' \
  -d '{"question":"Tell me about Kohli"}'
```

Run automated verification:

```bash
./mvnw verify
```

## Limitations And Future Improvements

- Name extraction supports a small set of comparison sentence forms. It is deliberately deterministic and may reject valid wording that does not match those forms.
- Partial-name search can require the caller to resubmit a more specific question. There is no follow-up request containing selected candidate IDs yet.
- `ballsFaced` currently includes wides because extras types are not persisted in the schema.
- The provider output is constrained by prompting but is still generated text. Future evaluation tests should check factual consistency against the supplied DTO.
- There is no AI-specific rate limiting, usage metering, retry policy, or circuit breaker yet.
- There is no streaming response and no AI comparison frontend page yet.
- The current adapter uses the OpenAI Responses API. Additional providers can implement `AiComparisonProvider` without changing matching or analytics logic.
- Future versions may add structured provider output or an explicit `summarySource` field so clients can distinguish provider text from fallback text.
- This feature should remain a grounded summarizer until a real multi-step tool-calling use case justifies an agentic layer.
