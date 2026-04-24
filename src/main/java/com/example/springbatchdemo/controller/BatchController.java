package com.example.springbatchdemo.controller;

import com.example.springbatchdemo.config.BatchConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchController {

    private final JobLauncher jobLauncher;
    private final Job productSyncJob;

    @PostMapping("/sync-products")
    public ResponseEntity<Map<String, Object>> syncProductsToElastic() {
        try {
            log.info("Ürün senkronizasyonu başlatılıyor...");
            JobParameters parameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            JobExecution execution = jobLauncher.run(productSyncJob, parameters);
            Map<String, Object> response = new HashMap<>();
            response.put("jobName", productSyncJob.getName());
            response.put("executionId", execution.getId());
            response.put("status", execution.getStatus().toString());
            response.put("startTime", execution.getStartTime().toString());

            log.info("Job başarıyla tetiklendi. Status: {}", execution.getStatus());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Batch Job başlatılırken bir hata oluştu!", e);

            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", "Job başlatılamadı");
            errorBody.put("details", e.getMessage());

            return ResponseEntity.internalServerError().body(errorBody);
        }
    }
}