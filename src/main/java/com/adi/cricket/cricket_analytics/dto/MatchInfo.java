package com.adi.cricket.cricket_analytics.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchInfo {

    private String city;

    private String venue;

    @JsonProperty("match_type")
    private String matchType;

    private List<String> dates;

    private List<String> teams;
    private Map<String, List<String>> players;
    private Registry registry;
}
