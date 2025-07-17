package com.pro2.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // 국가 코드와 통화 코드 매핑
    private static final Map<String, String> COUNTRY_TO_CURRENCY_MAP;
    // 국가 코드와 전체 국가명 매핑 (HTML 표시에 사용)
    private static final Map<String, String> COUNTRY_TO_NAME_MAP;
    // 국가 코드와 국기 이미지 파일명 매핑 (HTML 표시에 사용)
    private static final Map<String, String> COUNTRY_TO_FLAG_MAP;

    static {
        COUNTRY_TO_CURRENCY_MAP = new HashMap<>();
        COUNTRY_TO_CURRENCY_MAP.put("jp", "JPY(100)");
        COUNTRY_TO_CURRENCY_MAP.put("cn", "CNH"); // 중국 위안화 (역외 위안)
        COUNTRY_TO_CURRENCY_MAP.put("usca", "USD"); // 미국 캘리포니아 -> USD
        COUNTRY_TO_CURRENCY_MAP.put("usnj", "USD"); // 미국 뉴저지 -> USD
        COUNTRY_TO_CURRENCY_MAP.put("usor", "USD"); // 미국 오리건 -> USD
        COUNTRY_TO_CURRENCY_MAP.put("uk", "GBP");  // 영국 파운드
        COUNTRY_TO_CURRENCY_MAP.put("gr", "EUR");  // 독일 유로
        COUNTRY_TO_CURRENCY_MAP.put("au", "AUD");  // 호주 달러

        COUNTRY_TO_NAME_MAP = new HashMap<>();
        COUNTRY_TO_NAME_MAP.put("jp", "일본");
        COUNTRY_TO_NAME_MAP.put("cn", "중국");
        COUNTRY_TO_NAME_MAP.put("usca", "미국(CA)");
        COUNTRY_TO_NAME_MAP.put("usnj", "미국(NJ)");
        COUNTRY_TO_NAME_MAP.put("usor", "미국(OR)");
        COUNTRY_TO_NAME_MAP.put("uk", "영국");
        COUNTRY_TO_NAME_MAP.put("gr", "독일");
        COUNTRY_TO_NAME_MAP.put("au", "호주");

        COUNTRY_TO_FLAG_MAP = new HashMap<>();
        COUNTRY_TO_FLAG_MAP.put("jp", "japan.png");
        COUNTRY_TO_FLAG_MAP.put("cn", "china.png");
        COUNTRY_TO_FLAG_MAP.put("usca", "usa.png");
        COUNTRY_TO_FLAG_MAP.put("usnj", "usa.png");
        COUNTRY_TO_FLAG_MAP.put("usor", "usa.png");
        COUNTRY_TO_FLAG_MAP.put("uk", "uk.png");
        COUNTRY_TO_FLAG_MAP.put("gr", "germany.png");
        COUNTRY_TO_FLAG_MAP.put("au", "australia.png");
    }

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    // 배송 대행지 선택 페이지 (select.html을 위한 엔드포인트)
    @GetMapping("/select")
    public String showDeliverySelectionForm() {
        return "select";
    }

    // 신청 페이지 (write.html을 위한 엔드포인트 - 환율 정보 포함)
    @GetMapping("/write")
    public String showWriteForm(
            @RequestParam(name = "country", required = false) String countryCode,
            @RequestParam(name = "shippingMethod", required = false) String shippingMethod, // 배송 방법도 필요하다면 활용 가능
            Model model) {

        ExchangeRateDTO selectedExchangeRate = null;
        String currencyCode = null;

        // countryCode가 넘어왔을 경우에만 환율 정보 조회
        if (countryCode != null && !countryCode.isEmpty()) {
            currencyCode = COUNTRY_TO_CURRENCY_MAP.get(countryCode);
            String countryName = COUNTRY_TO_NAME_MAP.getOrDefault(countryCode, "알 수 없음");
            String flagImg = COUNTRY_TO_FLAG_MAP.getOrDefault(countryCode, "#.png"); // 기본 이미지

            if (currencyCode != null) {
                // 단일 통화에 대한 환율 조회
                List<ExchangeRateDTO> rates = exchangeRateService.getExchangeRatesWithChange(Collections.singletonList(currencyCode));
                if (rates != null && !rates.isEmpty()) {
                    selectedExchangeRate = rates.get(0);
                }
            }
            model.addAttribute("selectedCountryName", countryName);
            // cur_unit은 DTO에서 가져오는 것이 가장 정확하므로, selectedExchangeRate가 null이 아닐 때만 설정
            model.addAttribute("selectedCurrencyCode", (selectedExchangeRate != null) ? selectedExchangeRate.getCur_unit() : "N/A");
            model.addAttribute("selectedFlagImg", flagImg);
        } else {
            model.addAttribute("selectedCountryName", "국가 미선택");
            model.addAttribute("selectedCurrencyCode", "N/A");
            model.addAttribute("selectedFlagImg", "#.png"); // 기본 이미지
        }

        model.addAttribute("selectedExchangeRate", selectedExchangeRate);
        model.addAttribute("selectedShippingMethod", shippingMethod); // 배송 방법도 모델에 추가 (필요시)

        return "write";
    }
 // controller
    // 신청 버튼 클릭
    @PostMapping("/process")
    public String processForm(
            @RequestParam("action") String action,
            Model model) {

        if ("submit".equals(action)) {
            model.addAttribute("message", "신청이 완료되었습니다.");
        } else if ("save".equals(action)) {
            model.addAttribute("message", "임시 저장이 완료되었습니다.");
        } else {
            model.addAttribute("message", "알 수 없는 요청입니다.");
        }
        return "result";
    }
    
}