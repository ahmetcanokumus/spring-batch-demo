package com.example.springbatchdemo.processor;

import com.example.springbatchdemo.dto.CustomerCsvRecord;
import com.example.springbatchdemo.entity.Customer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class CustomerItemProcessor implements ItemProcessor<CustomerCsvRecord, Customer> {

    @Override
    public Customer process(@NonNull CustomerCsvRecord item) {
        if (item.getAge() == null || item.getAge() < 18) {
            return null;
        }
        return Customer.builder()
                .firstName(upper(item.getFirstName()))
                .lastName(upper(item.getLastName()))
                .email(lower(item.getEmail()))
                .age(item.getAge())
                .build();
    }

    private static String upper(String value) {
        return value == null ? null : value.toUpperCase();
    }

    private static String lower(String value) {
        return value == null ? null : value.toLowerCase();
    }
}
