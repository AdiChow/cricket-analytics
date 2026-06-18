package com.adi.cricket.cricket_analytics.service;

import com.adi.cricket.cricket_analytics.exception.InvalidAiComparisonException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlayerNameExtractorTest {

    private final PlayerNameExtractor extractor = new PlayerNameExtractor();

    @Test
    void extractsPlayersFromCompareQuestion() {
        assertThat(extractor.extractPlayerNames("Compare Kohli and Smith"))
                .containsExactly("Kohli", "Smith");
    }

    @Test
    void extractsFullNamesFromConversationalQuestion() {
        assertThat(extractor.extractPlayerNames(
                "How do Virat Kohli and Steve Smith compare?"
        )).containsExactly("Virat Kohli", "Steve Smith");
    }

    @Test
    void rejectsQuestionWithFewerThanTwoPlayerNames() {
        assertThatThrownBy(() ->
                extractor.extractPlayerNames("Tell me about Kohli")
        )
                .isInstanceOf(InvalidAiComparisonException.class)
                .hasMessageContaining("two players");
    }
}
