package com.adi.cricket.cricket_analytics.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Registry {

        private Map<String,String> people;
    }


