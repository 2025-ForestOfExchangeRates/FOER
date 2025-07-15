package com.pro2.tariff.controller;

import com.pro2.tariff.entity.TariffCut;
import com.pro2.tariff.service.TariffCutService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TariffController {

    private final TariffCutService service;

    public TariffController(TariffCutService service) {
        this.service = service;
    }

    @GetMapping("/select-country")
    public String selectCountryPage() {
        return "selectCountry"; // selectCountry.jsp
    }

    @GetMapping("/tariff-chart")
    public String showTariffChart(@RequestParam("country") String country, Model model) {
        List<TariffCut> cuts = service.getLastThreeWeeksCuts(country);

        List<String> dates = cuts.stream()
                                 .map(c -> c.getDate().toString())
                                 .collect(Collectors.toList());

        List<Double> values = cuts.stream()
                                  .map(TariffCut::getTariffCut)
                                  .collect(Collectors.toList());

        // 최신 관세 값 구하기 (가장 최근 날짜 데이터)
        Double latestTariff = null;
        if (!cuts.isEmpty()) {
            latestTariff = cuts.get(cuts.size() - 1).getTariffCut();
        }

        model.addAttribute("country", country);
        model.addAttribute("dates", dates);
        model.addAttribute("values", values);
        model.addAttribute("latestTariff", latestTariff);

        return "tariffChart"; // tariffChart.jsp
    }
}
