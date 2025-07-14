package com.pro2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration; // 이 줄을 추가합니다.

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) // 이 부분을 추가합니다.
public class Jun0901Application {

	public static void main(String[] args) {
		SpringApplication.run(Jun0901Application.class, args);
	}

}