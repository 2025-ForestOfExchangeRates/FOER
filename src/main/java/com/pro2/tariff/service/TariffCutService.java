package com.pro2.tariff.service;

import com.pro2.tariff.entity.TariffCut;
import com.pro2.tariff.repository.TariffCutRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TariffCutService {

    private final TariffCutRepository repository;

    public TariffCutService(TariffCutRepository repository) {
        this.repository = repository;
    }

    // 최근 3주 데이터 조회
    public List<TariffCut> getLastThreeWeeksCuts(String country) {
        return repository.findByCountryOrderByDateDesc(country)
                         .stream()
                         .limit(3)
                         .toList();
    }

    // 최신 한 건 조회
    public TariffCut getLatestCut(String country) {
        List<TariffCut> cuts = repository.findByCountryOrderByDateDesc(country);
        return cuts.isEmpty() ? null : cuts.get(0);
    }
}
