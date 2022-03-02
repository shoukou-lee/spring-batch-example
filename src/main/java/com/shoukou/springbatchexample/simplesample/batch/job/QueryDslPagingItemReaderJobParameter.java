package com.shoukou.springbatchexample.simplesample.batch.job;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.*;

@Getter
@Slf4j
@NoArgsConstructor
public class QueryDslPagingItemReaderJobParameter {
    private LocalDate lastAccess;

    @Value("#{jobParameters[lastAccess]}")
    public void setLastAccess(String lastAccess) {
        this.lastAccess = parse(lastAccess, ofPattern("yyyy-MM-dd"));
    }
}
