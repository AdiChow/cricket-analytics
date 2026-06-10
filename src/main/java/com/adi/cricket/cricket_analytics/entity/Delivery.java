package com.adi.cricket.cricket_analytics.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long matchId;

    private Integer inningsNumber;

    private Integer overNumber;

    private Integer ballNumber;

    private Long batterId;

    private Long bowlerId;

    private Long nonStrikerId;

    private Integer batterRuns;

    private Integer extraRuns;

    private Integer totalRuns;
}
