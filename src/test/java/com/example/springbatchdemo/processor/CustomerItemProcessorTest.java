package com.example.springbatchdemo.processor;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.springbatchdemo.dto.CustomerCsvRecord;
import com.example.springbatchdemo.entity.Customer;
import org.junit.jupiter.api.Test;

class CustomerItemProcessorTest {

    private final CustomerItemProcessor processor = new CustomerItemProcessor();

    @Test
    void transformsAndKeepsAdults() {
        CustomerCsvRecord in =
                CustomerCsvRecord.builder()
                        .firstName("mehmet")
                        .lastName("demir")
                        .email("Mehmet.Demir@MAIL.com")
                        .age(35)
                        .build();

        Customer out = processor.process(in);

        assertThat(out).isNotNull();
        assertThat(out.getFirstName()).isEqualTo("MEHMET");
        assertThat(out.getLastName()).isEqualTo("DEMIR");
        assertThat(out.getEmail()).isEqualTo("mehmet.demir@mail.com");
        assertThat(out.getAge()).isEqualTo(35);
    }

    @Test
    void filtersMinors() {
        CustomerCsvRecord in =
                CustomerCsvRecord.builder()
                        .firstName("Zeynep")
                        .lastName("Kaya")
                        .email("zeynep@test.org")
                        .age(16)
                        .build();

        assertThat(processor.process(in)).isNull();
    }
}
