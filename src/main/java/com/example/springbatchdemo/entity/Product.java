package com.example.springbatchdemo.entity;

import com.example.springbatchdemo.entity.enums.ApprovalStatus;
import com.example.springbatchdemo.entity.enums.ProductVariantType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(unique = true)
    protected String code;

    protected String name;
    protected String description;
    protected String title;
    protected String color;

    @ManyToOne
    @JoinColumn(name = "parent_product_id")
    private Product parentProduct;

    @OneToMany(mappedBy = "parentProduct", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    protected Set<Product> variants;

    @Enumerated(EnumType.STRING)
    private ProductVariantType variantType;

    private String size;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<PriceRow> priceRows;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private StockLevel stockLevel;

}