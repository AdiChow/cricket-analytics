package com.adi.cricket.cricket_analytics.service;

import com.adi.cricket.cricket_analytics.exception.InvalidAiComparisonException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PlayerNameExtractor {

    private static final List<Pattern> QUESTION_PATTERNS = List.of(
            Pattern.compile(
                    "^\\s*compare\\s+(.+?)\\s+(?:and|vs\\.?|versus|with)\\s+(.+?)\\s*[?.!]*\\s*$",
                    Pattern.CASE_INSENSITIVE
            ),
            Pattern.compile(
                    "^\\s*how\\s+(?:do|does)\\s+(.+?)\\s+and\\s+(.+?)\\s+compare\\s*[?.!]*\\s*$",
                    Pattern.CASE_INSENSITIVE
            ),
            Pattern.compile(
                    "^\\s*(.+?)\\s+(?:vs\\.?|versus)\\s+(.+?)\\s*[?.!]*\\s*$",
                    Pattern.CASE_INSENSITIVE
            )
    );

    public List<String> extractPlayerNames(String question) {
        if (question == null || question.isBlank()) {
            throw new InvalidAiComparisonException(
                    "question must not be blank"
            );
        }

        for (Pattern pattern : QUESTION_PATTERNS) {
            Matcher matcher = pattern.matcher(question);
            if (matcher.matches()) {
                String player1 = cleanName(matcher.group(1));
                String player2 = cleanName(matcher.group(2));

                if (!player1.isBlank() && !player2.isBlank()) {
                    return List.of(player1, player2);
                }
            }
        }

        throw new InvalidAiComparisonException(
                "Question must name two players using 'and', 'vs', or 'versus'"
        );
    }

    private String cleanName(String value) {
        return value
                .trim()
                .replaceAll("^[\\\"']+|[\\\"'?.!,]+$", "")
                .trim();
    }
}
