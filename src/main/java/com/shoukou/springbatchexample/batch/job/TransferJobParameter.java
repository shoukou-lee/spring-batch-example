package com.shoukou.springbatchexample.batch.job;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Getter
public class TransferJobParameter {
    private LocalDate currentDate;

    // LocalDate는 자동 형변환이 안되서, String->LocalDate로 parse 해야 함
    public TransferJobParameter(String currentDate) {
        this.currentDate = LocalDate.parse(currentDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}