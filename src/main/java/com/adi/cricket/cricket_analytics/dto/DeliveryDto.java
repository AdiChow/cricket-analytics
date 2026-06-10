package com.adi.cricket.cricket_analytics.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeliveryDto {

    private String batter;

    private String bowler;

    @JsonProperty("non_striker")
    private String nonStriker;

    private RunsDto runs;
}
