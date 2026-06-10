package com.adi.cricket.cricket_analytics.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RunsDto {

    private Integer batter;

    private Integer extras;

    private Integer total;
}
