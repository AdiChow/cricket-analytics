package com.adi.cricket.cricket_analytics.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InningsDto {

    private String team;

    private List<OverDto> overs;
}
