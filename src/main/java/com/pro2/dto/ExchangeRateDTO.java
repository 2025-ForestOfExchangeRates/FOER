package com.pro2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // DTO에 없는 필드는 무시하도록 추가
public class ExchangeRateDTO {
    private Integer result; // 조회결과
    private String cur_unit; // 통화코드
    private String cur_nm; // 국가/통화명
    private String deal_bas_r; // 매매기준율

    // --- 새로 추가할 필드 (전날 대비 변동된 값 및 방향) ---
    private Double changeFromPreviousDay; // 전날 대비 변동된 값
    private String changeDirection;       // 변동 방향 ("UP", "DOWN", "SAME", "N/A")


}