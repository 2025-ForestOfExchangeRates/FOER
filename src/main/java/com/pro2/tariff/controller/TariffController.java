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

@GetMapping("/write")
public String showWritePage(@RequestParam("country") String country, Model model) {
    List<TariffCut> cuts = service.getLastThreeWeeksCuts(country);
    TariffCut latest = service.getLatestCut(country);

    List<String> dates = cuts.stream()
                             .map(c -> c.getDate().toString())
                             .collect(Collectors.toList());

    List<Double> values = cuts.stream()
                             .map(TariffCut::getTariffCut)
                             .collect(Collectors.toList());

    model.addAttribute("country", country);
    model.addAttribute("dates", dates);
    model.addAttribute("values", values);
    model.addAttribute("latestTariff", latest != null ? latest.getTariffCut() : "정보 없음");

    return "write";  // write.html 뷰 반환
}

}
