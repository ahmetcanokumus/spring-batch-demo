package com.example.springbatchdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CSV satirindan okunan ham kayit (Entity'den ayri tutulur).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerCsvRecord {

    private String firstName;
    private String lastName;
    private String email;
    private Integer age;
}
