package com.adi.cricket.cricket_analytics.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String cricsheetMatchId;

    private String venue;

    private String city;

    private String matchType;

    private LocalDate startDate;

    private LocalDate endDate;
}