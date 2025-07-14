package com.pro2.service;

import java.time.DayOfWeek; // 이 임포트는 현재 사용되지 않지만, 나중에 필요할 수 있어 둡니다.
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.pro2.dto.ExchangeRateDTO;

@Service
public class ExchangeRateService {

    @Value("${exim.api.key}")
    private String apiKey;

    @Value("${exim.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public ExchangeRateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 특정 날짜의 전체 환율 정보를 가져옵니다.
     * API 호출 실패 시 빈 리스트를 반환하거나 적절히 처리합니다.
     * @param dateString 조회할 날짜 (yyyyMMdd 형식)
     * @return 해당 날짜의 전체 환율 리스트
     */
    public List<ExchangeRateDTO> getExchangeRatesForDate(String dateString) {
        String url = apiUrl.replace("{authkey}", apiKey).replace("{searchdate}", dateString);
        System.out.println("DEBUG: API 호출 URL = " + url);

        try {
            ResponseEntity<List<ExchangeRateDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ExchangeRateDTO>>() {}
            );

            List<ExchangeRateDTO> rates = response.getBody();

            // rates가 null이거나 비어있으면 result:2 체크 전에 빈 리스트 반환
            if (rates == null || rates.isEmpty()) {
                System.err.println("API 호출 결과: " + dateString + " 날짜에 대한 응답 본문이 비어있거나 null입니다.");
                return Collections.emptyList();
            }

            // result가 2이면 데이터 없음으로 간주
            if (rates.get(0).getResult() != null && rates.get(0).getResult() == 2) {
                 System.err.println("API 호출 결과: " + dateString + " 날짜에 대한 데이터 없음 (result:2)");
                 return Collections.emptyList();
            }
            return rates;

        } catch (HttpClientErrorException e) {
            System.err.println("API 호출 중 클라이언트 오류 발생: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return Collections.emptyList();
        } catch (Exception e) {
            System.err.println("API 호출 중 예외 발생: " + e.getMessage());
            e.printStackTrace(); // 어떤 예외인지 확인용 (개발 중에만 사용)
            return Collections.emptyList();
        }
    }

    /**
     * 가장 최근의 영업일 날짜를 찾아 반환합니다.
     * 최대 7일 전까지 시도하여 데이터를 찾습니다.
     * @param startLocalDate 시작 날짜
     * @return 데이터를 찾은 가장 최근 영업일의 yyyyMMdd 문자열
     */
    private String findRecentBusinessDay(LocalDate startLocalDate) {
        LocalDate currentDate = startLocalDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        
        for (int i = 0; i < 7; i++) { // 최대 7일 전까지 시도
            String dateString = currentDate.format(formatter);
            List<ExchangeRateDTO> rates = getExchangeRatesForDate(dateString); // API 호출 시도

            // --- 수정된 부분 ---
            if (rates != null && !rates.isEmpty()) {
                // rates가 비어있지 않고, 첫 번째 요소의 result가 2가 아니면 유효한 데이터로 판단
                if (rates.get(0).getResult() == null || rates.get(0).getResult() != 2) {
                    System.out.println("DEBUG: 데이터가 있는 가장 최근 영업일 찾음: " + dateString);
                    return dateString;
                }
            }
            // --- 수정된 부분 끝 ---
            
            currentDate = currentDate.minusDays(1); // 하루씩 뒤로 이동
        }
        System.err.println("DEBUG: 최근 7일간 유효한 환율 데이터를 찾을 수 없습니다.");
        return null; // 7일간 데이터를 찾지 못함
    }


    /**
     * 현재 날짜의 환율 정보와 전날 대비 변동을 계산하여 반환합니다.
     * @param selectedCurrencies 선택된 통화 단위 (예: "USD", "JPY") 리스트
     * @return 변동률이 계산된 환율 리스트
     */
    public List<ExchangeRateDTO> getExchangeRatesWithChange(List<String> selectedCurrencies) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        String todayDateString = today.format(formatter);
        // 오늘의 환율 가져오기
        List<ExchangeRateDTO> todayRates = getExchangeRatesForDate(todayDateString);

        // --- 수정된 부분 ---
        // 오늘 날짜 데이터가 없거나 result:2인 경우 가장 최근 영업일의 데이터를 다시 찾음
        if (todayRates == null || todayRates.isEmpty() || (todayRates.get(0).getResult() != null && todayRates.get(0).getResult() == 2)) {
            System.err.println("오늘 날짜(" + todayDateString + ")의 환율 데이터를 가져올 수 없습니다. 가장 최근 영업일의 데이터를 찾습니다.");
            String actualTodayDateString = findRecentBusinessDay(today);
            if (actualTodayDateString == null) {
                System.err.println("DEBUG: 현재 또는 최근 영업일의 데이터를 찾을 수 없어 빈 리스트 반환.");
                return Collections.emptyList();
            }
            todayRates = getExchangeRatesForDate(actualTodayDateString);
            // 재시도 후에도 데이터가 없으면 빈 리스트 반환
            if (todayRates == null || todayRates.isEmpty() || (todayRates.get(0).getResult() != null && todayRates.get(0).getResult() == 2)) {
                return Collections.emptyList();
            }
        }
        // --- 수정된 부분 끝 ---

        // 이전 영업일의 날짜 찾기
        LocalDate previousDayCandidate = LocalDate.parse(todayDateString, formatter).minusDays(1); // 실제 조회된 오늘 날짜를 기준으로 이전 날짜 탐색
        String previousDateString = findRecentBusinessDay(previousDayCandidate);

        List<ExchangeRateDTO> previousRates = Collections.emptyList();
        if (previousDateString != null) {
            previousRates = getExchangeRatesForDate(previousDateString);
        }

        Map<String, ExchangeRateDTO> previousRatesMap = previousRates.stream()
            .filter(rate -> rate.getCur_unit() != null)
            .collect(Collectors.toMap(ExchangeRateDTO::getCur_unit, rate -> rate));

        return todayRates.stream()
                .filter(rate -> selectedCurrencies.contains(rate.getCur_unit()))
                .map(todayRate -> {
                    double todayDealBasR = Double.parseDouble(todayRate.getDeal_bas_r().replace(",", ""));
                    
                    ExchangeRateDTO previousRate = previousRatesMap.get(todayRate.getCur_unit());
                    if (previousRate != null) {
                        double previousDealBasR = Double.parseDouble(previousRate.getDeal_bas_r().replace(",", ""));
                        double change = todayDealBasR - previousDealBasR;
                        todayRate.setChangeFromPreviousDay(change);
                        if (change > 0) {
                            todayRate.setChangeDirection("UP");
                        } else if (change < 0) {
                            todayRate.setChangeDirection("DOWN");
                        } else {
                            todayRate.setChangeDirection("SAME");
                        }
                    } else {
                        todayRate.setChangeFromPreviousDay(0.0);
                        todayRate.setChangeDirection("N/A");
                    }
                    return todayRate;
                })
                .collect(Collectors.toList());
    }
}