package com.pro2.tariff.controller;

import com.pro2.tariff.entity.Tariff;
import com.pro2.tariff.service.TariffService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TariffController {

    private final TariffService service;

    public TariffController(TariffService service) {
        this.service = service;
    }

    @GetMapping("/tariff-chart")
    public String showTariffChart(@RequestParam("country") String country, Model model) {
        List<Tariff> cuts = service.getLastThreeWeeksCuts(country);
        Tariff latest = service.getLatestCut(country);

        List<String> dates = cuts.stream()
                .map(c -> c.getDate().toString())
                .collect(Collectors.toList());

        List<Double> values = cuts.stream()
                .map(Tariff::getTariffCut)
                .collect(Collectors.toList());

        model.addAttribute("country", country);
        model.addAttribute("dates", dates);
        model.addAttribute("values", values);
        model.addAttribute("latestTariff", latest != null ? latest.getTariffCut() : "정보 없음");

        return "tariffChart";
    }
}
