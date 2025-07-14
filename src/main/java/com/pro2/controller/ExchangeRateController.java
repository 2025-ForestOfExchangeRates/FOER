package com.pro2.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pro2.dto.ExchangeRateDTO;
import com.pro2.service.ExchangeRateService;

@Controller
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    // 1. 나라 선택 폼을 보여주는 페이지
    // 이 엔드포인트로 접근하여 사용자가 국가를 선택하고 "환율 조회"를 시작합니다.
    @GetMapping("/exchange-selection")
    public String showSelectionForm(Model model) {
        // 미국, 일본, 중국, 영국, 독일, 호주 (6개 국가)
        List<String> availableCurrencies = Arrays.asList("USD", "JPY(100)", "CNH", "GBP", "EUR", "AUD");
        model.addAttribute("availableCurrencies", availableCurrencies);
        return "exchangeSelection"; // src/main/resources/templates/exchangeSelection.html 템플릿 렌더링
    }

    // 2. 폼 데이터를 받아 환율 정보를 조회하고 결과를 보여주는 페이지
    // exchangeSelection.html에서 폼이 제출되면 이 엔드포인트가 호출됩니다.
    @PostMapping("/exchange-result")
    public String getExchangeRates(
            @RequestParam(name = "currencies", required = false) List<String> selectedCurrencies,
            Model model) {

        // 선택된 통화가 없으면 기본 6개 통화 모두를 조회하도록 설정합니다.
        if (selectedCurrencies == null || selectedCurrencies.isEmpty()) {
            selectedCurrencies = Arrays.asList("USD", "JPY(100)", "CNH", "GBP", "EUR", "AUD");
            model.addAttribute("message", "선택된 통화가 없어 기본 6개 통화가 조회되었습니다.");
        }

        // ExchangeRateService를 통해 선택된 통화들의 현재 환율과 전날 대비 변동률을 가져옵니다.
        List<ExchangeRateDTO> exchangeRates = exchangeRateService.getExchangeRatesWithChange(selectedCurrencies);

        // 결과 페이지에 표시될 조회 날짜 (오늘 날짜)를 모델에 추가합니다.
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        model.addAttribute("selectedDate", today);

        model.addAttribute("exchangeRates", exchangeRates);

        return "exchangeResult"; 
    }
    // 배송 대행지 선택 페이지
    @GetMapping("/select")
    public String SelectionForm() {
    	return "select";
    }
    // 신청 페이지
    @GetMapping("/write")
    public String Write() {
    	return "write";
    }

    
}