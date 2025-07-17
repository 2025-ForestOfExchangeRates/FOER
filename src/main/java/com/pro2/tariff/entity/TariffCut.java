package com.pro2.tariff.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "tariff_table")
@Getter
@Setter
public class TariffCut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private String country;

    @Column(name = "tariff_cut") // 명시적으로 매핑
    private Double tariffCut;
}
