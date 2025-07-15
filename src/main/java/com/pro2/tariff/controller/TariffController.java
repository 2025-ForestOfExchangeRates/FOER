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

        model.addAttribute("country", country);
        model.addAttribute("dates", dates);
        model.addAttribute("values", values);

        return "tariffChart"; // tariffChart.jsp
    }
}
