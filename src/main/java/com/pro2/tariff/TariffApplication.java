package com.pro2.tariff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 크롤러 자동 실행
public class TariffApplication {
    public static void main(String[] args) {
        SpringApplication.run(TariffApplication.class, args);
    }
}
