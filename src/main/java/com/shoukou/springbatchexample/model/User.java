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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalDate lastAccess;
    private Boolean isDormant;

    @Builder
    public User(String name, LocalDate lastAccess) {
        this.name = name;
        this.lastAccess = lastAccess;
        this.isDormant = false;
    }

    public void setIsDormant(Boolean isDormant) {
        this.isDormant = isDormant;
    }

}
