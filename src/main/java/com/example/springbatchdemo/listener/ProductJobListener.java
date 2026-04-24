package com.example.springbatchdemo.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Slf4j
@Component
public class ProductJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("[JOB STARTED] Name: {} | Instance ID: {} | Start Time: {} | Parameters: {}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getJobId(),
                jobExecution.getStartTime(),
                jobExecution.getJobParameters());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        Duration duration = Duration.between(Objects.requireNonNull(jobExecution.getStartTime()), jobExecution.getEndTime());

        log.info("[JOB ENDED] Name: {} | Status: {} | Duration: {}ms ({}s) | Start: {} | End: {}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus(),
                duration.toMillis(),
                duration.getSeconds(),
                jobExecution.getStartTime(),
                jobExecution.getEndTime()
        );
        if (jobExecution.getStatus().equals(BatchStatus.FAILED)) {
            jobExecution.getAllFailureExceptions().forEach(e ->
                    log.error("Critical error in job execution: ", e)
            );
        }
    }

}