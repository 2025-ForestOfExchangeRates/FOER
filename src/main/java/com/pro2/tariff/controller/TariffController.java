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
    public String showTariffChart(@RequestParam("country") String countryCode, Model model) {
        // 코드 → 한글 국가명 매핑
        String countryName = convertCountryCodeToKorean(countryCode);

        if (countryName == null) {
            // 잘못된 국가코드 처리 (예: 에러 페이지, 또는 기본 국가 페이지로 redirect)
            return "error/invalidCountry"; // 예: error/invalidCountry.jsp
        }

        List<TariffCut> cuts = service.getLastThreeWeeksCuts(countryName);

        List<String> dates = cuts.stream()
                                 .map(c -> c.getDate().toString())
                                 .collect(Collectors.toList());

        List<Double> values = cuts.stream()
                                  .map(TariffCut::getTariffCut)
                                  .collect(Collectors.toList());

        Double latestTariff = null;
        if (!cuts.isEmpty()) {
            latestTariff = cuts.get(cuts.size() - 1).getTariffCut();
        }

        model.addAttribute("country", countryName);
        model.addAttribute("dates", dates);
        model.addAttribute("values", values);
        model.addAttribute("latestTariff", latestTariff);

        return "tariffChart"; // tariffChart.jsp
    }

    // 코드 → 한글 국가명 변환 함수 (default 제거)
    private String convertCountryCodeToKorean(String code) {
        return switch (code) {
            case "jp"   -> "일본";
            case "cn"   -> "중국";
            case "usca", "usnj", "usor" -> "미국";
            case "uk"   -> "영국";
            case "gr"   -> "독일";
            case "au"   -> "호주";
            default     -> null; // default 제거, 매핑 없으면 null 반환
        };
    }
}
