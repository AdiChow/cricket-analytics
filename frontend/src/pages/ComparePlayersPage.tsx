import { type FormEvent, useEffect, useRef, useState } from 'react'
import { ErrorState, LoadingState } from '../components/AsyncStates'
import { comparePlayers, searchPlayers } from '../lib/api'
import type {
  PlayerComparison,
  PlayerProfile,
  PlayerSearchResult,
} from '../types'

interface PlayerSelectorProps {
  label: string
  selectedPlayer: PlayerSearchResult | null
  onSelect: (player: PlayerSearchResult) => void
}

function PlayerSelector({
  label,
  selectedPlayer,
  onSelect,
}: PlayerSelectorProps) {
  const [query, setQuery] = useState('')
  const [results, setResults] = useState<PlayerSearchResult[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [hasSearched, setHasSearched] = useState(false)
  const requestController = useRef<AbortController | null>(null)

  useEffect(() => {
    return () => requestController.current?.abort()
  }, [])

  async function handleSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const trimmedQuery = query.trim()

    if (!trimmedQuery) {
      setError('Enter a player name to search.')
      return
    }

    requestController.current?.abort()
    const controller = new AbortController()
    requestController.current = controller
    setLoading(true)
    setError(null)
    setHasSearched(true)

    try {
      setResults(await searchPlayers(trimmedQuery, controller.signal))
    } catch (requestError) {
      if (!controller.signal.aborted) {
        setError(
          requestError instanceof Error
            ? requestError.message
            : 'Unable to search players',
        )
      }
    } finally {
      if (!controller.signal.aborted) {
        setLoading(false)
      }
    }
  }

  function selectPlayer(player: PlayerSearchResult) {
    onSelect(player)
    setQuery(player.playerName)
    setResults([])
    setError(null)
    setHasSearched(false)
  }

  return (
    <section className="rounded-2xl border border-line bg-white p-5 shadow-sm">
      <div className="flex items-center justify-between gap-4">
        <h2 className="text-lg font-black text-ink">{label}</h2>
        {selectedPlayer && (
          <span className="rounded-full bg-brand-soft px-3 py-1 text-xs font-bold text-brand-dark">
            Selected
          </span>
        )}
      </div>

      {selectedPlayer && (
        <div className="mt-4 rounded-xl bg-canvas p-4">
          <p className="font-black text-ink">{selectedPlayer.playerName}</p>
          <p className="mt-1 text-xs font-semibold uppercase tracking-wider text-muted">
            Player ID {selectedPlayer.playerId}
          </p>
        </div>
      )}

      <form onSubmit={handleSearch} className="mt-4 flex gap-2">
        <label htmlFor={`${label}-search`} className="sr-only">
          Search {label.toLowerCase()}
        </label>
        <input
          id={`${label}-search`}
          value={query}
          onChange={(event) => setQuery(event.target.value)}
          placeholder="Search by player name"
          className="min-w-0 flex-1 rounded-xl border border-line bg-canvas px-4 py-3 text-sm font-medium text-ink outline-none placeholder:text-muted/70 focus:border-brand focus:ring-2 focus:ring-brand/20"
        />
        <button
          type="submit"
          disabled={loading}
          className="rounded-xl bg-brand px-4 py-3 text-sm font-black text-white hover:bg-brand-dark disabled:cursor-not-allowed disabled:opacity-60"
        >
          {loading ? 'Searching...' : 'Search'}
        </button>
      </form>

      {error && <p className="mt-3 text-sm font-semibold text-red-700">{error}</p>}

      {!error && hasSearched && !loading && results.length === 0 && (
        <p className="mt-3 text-sm text-muted">No matching players found.</p>
      )}

      {results.length > 0 && (
        <div className="mt-3 max-h-64 space-y-2 overflow-y-auto">
          {results.map((player) => (
            <button
              key={player.playerId}
              type="button"
              onClick={() => selectPlayer(player)}
              className="flex w-full items-center justify-between rounded-xl border border-line px-4 py-3 text-left hover:border-brand/40 hover:bg-brand-soft/40"
            >
              <span className="font-bold text-ink">{player.playerName}</span>
              <span className="text-xs font-semibold text-muted">
                ID {player.playerId}
              </span>
            </button>
          ))}
        </div>
      )}
    </section>
  )
}

function PlayerStatCard({
  profile,
  position,
}: {
  profile: PlayerProfile
  position: string
}) {
  const metrics = [
    { label: 'Matches', value: profile.matches.toLocaleString() },
    { label: 'Runs', value: profile.runs.toLocaleString() },
    { label: 'Balls faced', value: profile.ballsFaced.toLocaleString() },
    { label: 'Strike rate', value: profile.strikeRate.toFixed(2) },
  ]

  return (
    <article className="overflow-hidden rounded-2xl border border-line bg-white shadow-sm">
      <div className="bg-ink px-6 py-6 text-white">
        <p className="text-xs font-bold uppercase tracking-[0.16em] text-[#78d6a6]">
          {position}
        </p>
        <h3 className="mt-2 text-2xl font-black tracking-tight">
          {profile.playerName}
        </h3>
        <p className="mt-1 text-xs text-white/60">Player ID {profile.playerId}</p>
      </div>
      <dl className="grid grid-cols-2 gap-px bg-line">
        {metrics.map((metric) => (
          <div key={metric.label} className="bg-white p-5">
            <dt className="text-xs font-bold uppercase tracking-wider text-muted">
              {metric.label}
            </dt>
            <dd className="mt-2 text-2xl font-black tabular-nums text-ink">
              {metric.value}
            </dd>
          </div>
        ))}
      </dl>
    </article>
  )
}

export function ComparePlayersPage() {
  const [player1, setPlayer1] = useState<PlayerSearchResult | null>(null)
  const [player2, setPlayer2] = useState<PlayerSearchResult | null>(null)
  const [comparison, setComparison] = useState<PlayerComparison | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const requestController = useRef<AbortController | null>(null)

  useEffect(() => {
    return () => requestController.current?.abort()
  }, [])

  function selectPlayer(
    position: 'player1' | 'player2',
    player: PlayerSearchResult,
  ) {
    if (position === 'player1') {
      setPlayer1(player)
    } else {
      setPlayer2(player)
    }

    requestController.current?.abort()
    setComparison(null)
    setError(null)
    setLoading(false)
  }

  async function loadComparison() {
    if (!player1 || !player2) {
      setError('Select two players before comparing.')
      return
    }

    if (player1.playerId === player2.playerId) {
      setError('Choose two different players to compare.')
      return
    }

    requestController.current?.abort()
    const controller = new AbortController()
    requestController.current = controller
    setLoading(true)
    setError(null)
    setComparison(null)

    try {
      setComparison(
        await comparePlayers(player1.playerId, player2.playerId, controller.signal),
      )
    } catch (requestError) {
      if (!controller.signal.aborted) {
        setError(
          requestError instanceof Error
            ? requestError.message
            : 'Unable to compare players',
        )
      }
    } finally {
      if (!controller.signal.aborted) {
        setLoading(false)
      }
    }
  }

  return (
    <div className="mx-auto max-w-6xl">
      <div className="max-w-3xl">
        <p className="text-xs font-bold uppercase tracking-[0.18em] text-brand">
          Player comparison
        </p>
        <h1 className="mt-3 text-4xl font-black tracking-[-0.035em] text-ink sm:text-5xl">
          Compare batting profiles
        </h1>
        <p className="mt-4 text-base leading-7 text-muted">
          Search and select two players to compare matches, runs, balls faced,
          and strike rate from the live analytics API.
        </p>
      </div>

      <div className="mt-8 grid gap-5 lg:grid-cols-2">
        <PlayerSelector
          label="Player 1"
          selectedPlayer={player1}
          onSelect={(player) => selectPlayer('player1', player)}
        />
        <PlayerSelector
          label="Player 2"
          selectedPlayer={player2}
          onSelect={(player) => selectPlayer('player2', player)}
        />
      </div>

      <div className="mt-5 flex justify-center">
        <button
          type="button"
          onClick={() => void loadComparison()}
          disabled={!player1 || !player2 || loading}
          className="w-full rounded-xl bg-brand px-7 py-3.5 text-sm font-black text-white hover:bg-brand-dark disabled:cursor-not-allowed disabled:opacity-50 sm:w-auto"
        >
          {loading ? 'Comparing...' : 'Compare players'}
        </button>
      </div>

      <div className="mt-8">
        {loading && <LoadingState label="Loading player comparison..." />}

        {!loading && error && (
          <ErrorState
            message={error}
            onRetry={player1 && player2 ? () => void loadComparison() : undefined}
          />
        )}

        {!loading && !error && comparison && (
          <div className="grid gap-5 lg:grid-cols-2">
            <PlayerStatCard profile={comparison.player1} position="Player 1" />
            <PlayerStatCard profile={comparison.player2} position="Player 2" />
          </div>
        )}

        {!loading && !error && !comparison && (
          <div className="rounded-2xl border border-dashed border-line bg-white p-8 text-center">
            <p className="font-black text-ink">No comparison loaded</p>
            <p className="mt-1 text-sm text-muted">
              Select two different players and choose Compare players.
            </p>
          </div>
        )}
      </div>
    </div>
  )
}
