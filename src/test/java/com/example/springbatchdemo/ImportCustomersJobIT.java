package com.example.springbatchdemo;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.springbatchdemo.config.BatchConfig;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ImportCustomersJobIT {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("importCustomersJob")
    private Job importCustomersJob;

    @Test
    void importCustomersJobCompletes() throws Exception {
        JobParameters parameters =
                new JobParametersBuilder().addLong("run.id", System.currentTimeMillis()).toJobParameters();

        JobExecution execution = jobLauncher.run(importCustomersJob, parameters);

        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(execution.getJobInstance().getJobName()).isEqualTo(BatchConfig.JOB_NAME);
    }
}
