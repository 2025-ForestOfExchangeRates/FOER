package com.pro2.config;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        // Apache HttpClient 5.x를 기반으로 RestTemplate을 구성합니다.
        // 이를 통해 SSL/TLS 관련 문제를 더 세밀하게 제어할 수 있습니다.
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        // HttpComponentsClientHttpRequestFactory를 사용하여 RestTemplate에 HttpClient를 연결합니다.
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClientBuilder.build());

        // 선택 사항: 연결 및 읽기 타임아웃 설정 (필요시 조정)
        factory.setConnectTimeout(5000); // 연결 시도 시 5초 타임아웃
        factory.setReadTimeout(10000);   // 데이터 읽기 시 10초 타임아웃

        return new RestTemplate(factory);
    }
}