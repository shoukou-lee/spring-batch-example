package com.shoukou.springbatchexample.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class DormantUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long originId;

    private String name;
    private LocalDate lastAccess;

    @Builder
    public DormantUser(User user) {
        this.originId = user.getId();
        this.name = user.getName();
        this.lastAccess = user.getLastAccess();
    }
}
