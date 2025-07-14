package com.pro2.tariff.repository;


import com.pro2.tariff.entity.TariffCut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TariffCutRepository extends JpaRepository<TariffCut, Long> {

    @Query("SELECT t FROM TariffCut t WHERE t.date >= :startDate AND t.country = :country ORDER BY t.date ASC")
    List<TariffCut> findByDateAfterAndCountryOrderByDateAsc(@Param("startDate") LocalDate startDate, @Param("country") String country);
}