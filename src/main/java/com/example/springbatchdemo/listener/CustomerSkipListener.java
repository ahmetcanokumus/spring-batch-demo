package com.example.springbatchdemo.listener;

import com.example.springbatchdemo.dto.CustomerCsvRecord;
import com.example.springbatchdemo.entity.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.lang.NonNull;

public class CustomerSkipListener implements SkipListener<CustomerCsvRecord, Customer> {

    private static final Logger log = LoggerFactory.getLogger(CustomerSkipListener.class);

    @Override
    public void onSkipInRead(@NonNull Throwable t) {
        log.warn("CSV satiri okunurken atlandi: {}", t.getMessage());
    }

    @Override
    public void onSkipInProcess(@NonNull CustomerCsvRecord item, @NonNull Throwable t) {
        log.warn("Islem sirasinda kayit atlandi [{} {}]: {}", item.getEmail(), item.getAge(), t.getMessage());
    }

    @Override
    public void onSkipInWrite(@NonNull Customer item, @NonNull Throwable t) {
        log.warn("Yazma sirasinda kayit atlandi [{}]: {}", item.getEmail(), t.getMessage());
    }
}
