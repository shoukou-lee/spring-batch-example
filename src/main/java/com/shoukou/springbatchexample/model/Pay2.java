package com.shoukou.springbatchexample.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Pay2 {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long amount;
    private String txName;
    private LocalDateTime txDateTime;

    public Pay2(Long amount, String txName, LocalDateTime txDateTime) {
        this.amount = amount;
        this.txName = txName;
        this.txDateTime = txDateTime;
    }

}
