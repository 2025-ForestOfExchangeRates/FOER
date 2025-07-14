package com.pro2.tariff.service;


import com.pro2.tariff.entity.TariffCut;
import com.pro2.tariff.repository.TariffCutRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class TariffCrawlerService {

    private final TariffCutRepository repository;

    public TariffCrawlerService(TariffCutRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "0 0 3 ? * MON") // 매주 월요일 오전 3시
    public void crawlAndSaveTariffs() {
        try {
            String url = "https://www.customs.go.kr/kcs/ad/tax/BuyTaxCalculation.do";
            Document doc = Jsoup.connect(url).get();

            List<String> countryList = Arrays.asList("일본", "미국", "영국", "호주", "독일", "중국");

            Elements tdElements = doc.select("td.al");
            for (Element td : tdElements) {
                Element span = td.selectFirst("span.select2-selection__rendered");
                if (span == null) continue;

                String country = span.attr("title").trim();
                if (!countryList.contains(country)) continue;

                Element div = td.selectFirst("div#ww");
                if (div == null) continue;

                String valueStr = div.text().trim();
                if (valueStr.isEmpty()) continue;

                Double tariffValue = Double.parseDouble(valueStr.replace(",", ""));

                TariffCut tariff = new TariffCut();
                tariff.setDate(LocalDate.now());
                tariff.setCountry(country);
                tariff.setTariffCut(tariffValue);

                repository.save(tariff);
                System.out.println("[" + country + "] 관세 저장 완료: " + tariffValue);
            }

            System.out.println("=== 관세 크롤링 완료 ===");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}