package com.pro2.tariff.service;

import com.pro2.tariff.entity.TariffCut;
import com.pro2.tariff.repository.TariffCutRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TariffCutService {

    private final TariffCutRepository repository;

    public TariffCutService(TariffCutRepository repository) {
        this.repository = repository;
    }

    public List<TariffCut> getLastThreeWeeksCuts(String country) {
        LocalDate threeWeeksAgo = LocalDate.now().minusWeeks(3);
        return repository.findByDateAfterAndCountryOrderByDateAsc(threeWeeksAgo, country);
    }
}