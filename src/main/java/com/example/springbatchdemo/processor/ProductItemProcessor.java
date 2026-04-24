package com.example.springbatchdemo.processor;

import com.example.springbatchdemo.dto.ProductDocument;
import com.example.springbatchdemo.dto.ProductSizeVariant;
import com.example.springbatchdemo.entity.PriceRow;
import com.example.springbatchdemo.entity.Product;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import java.util.List;
@Component
public class ProductItemProcessor implements ItemProcessor<Product, ProductDocument> {

    @Override
    public ProductDocument process(Product colorProduct) {
        List<ProductSizeVariant> availableSizes = colorProduct.getVariants().stream()
                .filter(v -> v.getStockLevel() != null && v.getStockLevel().getAvailable() > 0)
                .map(v -> new ProductSizeVariant(
                        v.getCode(),
                        v.getSize(),
                        findActivePrice(v),
                        v.getStockLevel().getAvailable()
                ))
                .toList();
        if (availableSizes.isEmpty()) {
            return null;
        }
        return new ProductDocument(
                colorProduct.getCode(),
                colorProduct.getParentProduct() != null ? colorProduct.getParentProduct().getCode() : colorProduct.getCode(),
                colorProduct.getName(),
                colorProduct.getColor(),
                colorProduct.getDescription(),
                availableSizes);
    }

    private Double findActivePrice(Product product) {
        if (product.getPriceRows() == null || product.getPriceRows().isEmpty()) {
            return 0.0;
        }
        return product.getPriceRows().stream()
                .filter(price -> (price.getStartDate() == null || price.getStartDate().isBefore(LocalDateTime.now())) &&
                        (price.getEndDate() == null || price.getEndDate().isAfter(LocalDateTime.now())))
                .map(PriceRow::getPrice)
                .findFirst()
                .orElse(0.0);
    }

}