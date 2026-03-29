package com.example.springbatchdemo.config;

import com.example.springbatchdemo.dto.CustomerCsvRecord;
import com.example.springbatchdemo.entity.Customer;
import com.example.springbatchdemo.listener.BatchJobLifecycleListener;
import com.example.springbatchdemo.listener.BatchStepMetricsListener;
import com.example.springbatchdemo.listener.CustomerSkipListener;
import com.example.springbatchdemo.processor.CustomerItemProcessor;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    public static final String JOB_NAME = "importCustomersJob";
    public static final String STEP_NAME = "importCustomersStep";

    @Bean
    public FlatFileItemReader<CustomerCsvRecord> customerReader() {
        BeanWrapperFieldSetMapper<CustomerCsvRecord> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(CustomerCsvRecord.class);

        return new FlatFileItemReaderBuilder<CustomerCsvRecord>()
                .name("customerReader")
                .resource(new ClassPathResource("customers.csv"))
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names("firstName", "lastName", "email", "age")
                .fieldSetMapper(fieldSetMapper)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Customer> customerWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Customer>()
                .dataSource(dataSource)
                .sql(
                        """
                        INSERT INTO customers (first_name, last_name, email, age)
                        VALUES (:firstName, :lastName, :email, :age)
                        """)
                .beanMapped()
                .assertUpdates(true)
                .build();
    }

    @Bean
    public Step importCustomersStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<CustomerCsvRecord> customerReader,
            CustomerItemProcessor customerItemProcessor,
            JdbcBatchItemWriter<Customer> customerWriter,
            BatchStepMetricsListener batchStepMetricsListener,
            CustomerSkipListener customerSkipListener) {

        return new StepBuilder(STEP_NAME, jobRepository)
                .<CustomerCsvRecord, Customer>chunk(10, transactionManager)
                .reader(customerReader)
                .processor(customerItemProcessor)
                .writer(customerWriter)
                .faultTolerant()
                .skip(FlatFileParseException.class)
                .skip(NumberFormatException.class)
                .skip(IllegalArgumentException.class)
                .skipLimit(100)
                .listener(customerSkipListener)
                .listener(batchStepMetricsListener)
                .build();
    }

    @Bean
    public Job importCustomersJob(
            JobRepository jobRepository,
            Step importCustomersStep,
            BatchJobLifecycleListener batchJobLifecycleListener) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(importCustomersStep)
                .listener(batchJobLifecycleListener)
                .build();
    }

    @Bean
    public BatchStepMetricsListener batchStepMetricsListener() {
        return new BatchStepMetricsListener();
    }

    @Bean
    public BatchJobLifecycleListener batchJobLifecycleListener() {
        return new BatchJobLifecycleListener();
    }

    @Bean
    public CustomerSkipListener customerSkipListener() {
        return new CustomerSkipListener();
    }
}
