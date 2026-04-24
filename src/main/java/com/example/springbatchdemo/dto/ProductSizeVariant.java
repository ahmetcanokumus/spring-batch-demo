package com.example.springbatchdemo.dto;

public record ProductSizeVariant(
        String code,
        String size,
        Double price,
        Integer stock
) {}