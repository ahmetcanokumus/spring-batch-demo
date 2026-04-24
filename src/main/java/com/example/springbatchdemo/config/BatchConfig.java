package com.example.springbatchdemo.config;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import com.example.springbatchdemo.dto.ProductDocument;
import com.example.springbatchdemo.entity.Product;

import com.example.springbatchdemo.listener.ProductJobListener;
import com.example.springbatchdemo.listener.ProductSkipListener;
import com.example.springbatchdemo.processor.ProductItemProcessor;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;


@Configuration
public class BatchConfig {

    public static final String JOB_NAME = "productToElasticJob";
    public static final String STEP_NAME = "productToElasticStep";

    @Bean
    public JpaPagingItemReader<Product> productReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Product>()
                .name("productReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        SELECT DISTINCT p FROM Product p 
                        LEFT JOIN FETCH p.variants v
                        LEFT JOIN FETCH p.priceRows pr
                        JOIN p.stockLevel s
                        WHERE p.variantType = :vType 
                        AND p.approvalStatus = :status
                        AND s.available > 0
                        """)
                .parameterValues(Map.of(
                        "vType", com.example.springbatchdemo.entity.enums.ProductVariantType.COLOR,
                        "status", com.example.springbatchdemo.entity.enums.ApprovalStatus.APPROVED
                ))
                .pageSize(100)
                .transacted(false)
                .build();
    }

    @Bean
    public ItemWriter<ProductDocument> elasticsearchWriter(ElasticsearchOperations elasticsearchOperations) {
        return items -> {
            if (!items.isEmpty()) {
                elasticsearchOperations.save(items.getItems());
            }
        };
    }

    @Bean
    public Step productToElasticStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            JpaPagingItemReader<Product> productReader,
            ProductItemProcessor productItemProcessor,
            ItemWriter<ProductDocument> elasticsearchWriter,
            ProductSkipListener productSkipListener) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<Product, ProductDocument>chunk(10, transactionManager)
                .reader(productReader)
                .processor(productItemProcessor)
                .writer(elasticsearchWriter)
                .faultTolerant()
                .skip(Exception.class)
                .retry(ElasticsearchException.class)
                .retryLimit(3)
                .listener(productSkipListener)
                .skipLimit(50)
                .build();
    }

    @Bean
    public Job productToElasticJob(JobRepository jobRepository, Step productToElasticStep, ProductJobListener productJobListener) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(productToElasticStep)
                .listener(productJobListener)
                .build();
    }

}
