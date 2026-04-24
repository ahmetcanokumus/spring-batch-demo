package com.example.springbatchdemo.dto;

import jakarta.persistence.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import java.util.List;

@Document(indexName = "products")
public record ProductDocument(
        @Id
        String id,
        String baseCode,
        String name,
        String color,
        String description,
        List<ProductSizeVariant> variants
) {}