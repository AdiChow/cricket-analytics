package com.adi.cricket.cricket_analytics.ai;

import com.adi.cricket.cricket_analytics.dto.PlayerComparisonDto;

public interface AiComparisonProvider {

    String generateComparisonSummary(
            String question,
            PlayerComparisonDto stats
    );
}
