package com.example.springbatchdemo.controller;

import com.example.springbatchdemo.config.BatchConfig;
import java.util.HashMap;
import java.util.Map;
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

@RestController
@RequestMapping("/api/batch")
public class BatchController {

    private final JobLauncher jobLauncher;
    private final Job importCustomersJob;

    public BatchController(JobLauncher jobLauncher, @Qualifier("importCustomersJob") Job importCustomersJob) {
        this.jobLauncher = jobLauncher;
        this.importCustomersJob = importCustomersJob;
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startJob() throws Exception {
        JobParameters parameters =
                new JobParametersBuilder().addLong("run.id", System.currentTimeMillis()).toJobParameters();

        JobExecution execution = jobLauncher.run(importCustomersJob, parameters);

        Map<String, Object> body = new HashMap<>();
        body.put("jobName", BatchConfig.JOB_NAME);
        body.put("executionId", execution.getId());
        body.put("status", execution.getStatus().name());
        return ResponseEntity.ok(body);
    }
}
