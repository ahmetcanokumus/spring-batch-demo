package com.example.springbatchdemo.listener;

import com.example.springbatchdemo.dto.ProductDocument;
import com.example.springbatchdemo.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductSkipListener implements SkipListener<Product, ProductDocument> {

    @Override
    public void onSkipInRead(Throwable t) {
        log.error(">>> [READING ERROR] An error occurred while reading the record and it was skipped. Error: ", t);
    }

    @Override
    public void onSkipInProcess(Product item, Throwable t) {
        log.error("[PROCESSING ERROR] An error occurred while processing the product: {}. Product ID: {}",
                item.getName(), item.getId(), t);
    }

    @Override
    public void onSkipInWrite(ProductDocument item, Throwable t) {
        log.error(">>> [YAZMA HATASI] Elasticsearch'e yazılırken hata oluştu: {}. Hata: {}",
                item.id(), t.getMessage());
    }
}