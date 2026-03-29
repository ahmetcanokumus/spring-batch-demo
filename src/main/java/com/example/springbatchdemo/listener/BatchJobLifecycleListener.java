package com.example.springbatchdemo.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.lang.NonNull;

/**
 * Job baslangic ve bitis loglari (mulakat isteri: JobExecutionListener).
 */
public class BatchJobLifecycleListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(BatchJobLifecycleListener.class);

    @Override
    public void beforeJob(@NonNull JobExecution jobExecution) {
        log.info(
                "Job '{}' basladi, executionId={}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getId());
    }

    @Override
    public void afterJob(@NonNull JobExecution jobExecution) {
        log.info(
                "Job '{}' tamamlandi, executionId={}, status={}, exitStatus={}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getId(),
                jobExecution.getStatus(),
                jobExecution.getExitStatus());
    }
}
