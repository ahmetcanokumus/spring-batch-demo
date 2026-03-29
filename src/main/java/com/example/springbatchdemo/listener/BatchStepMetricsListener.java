package com.example.springbatchdemo.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.lang.NonNull;

/**
 * Her step basinda ve sonunda islenen kayit sayilarini loglar.
 */
public class BatchStepMetricsListener implements StepExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(BatchStepMetricsListener.class);

    @Override
    public void beforeStep(@NonNull StepExecution stepExecution) {
        log.info(
                "Step '{}' basladi [okuma={}, yazma={}, skip={}, filter={}]",
                stepExecution.getStepName(),
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                stepExecution.getSkipCount(),
                stepExecution.getFilterCount());
    }

    @Override
    public ExitStatus afterStep(@NonNull StepExecution stepExecution) {
        log.info(
                "Step '{}' bitti [okuma={}, yazma={}, skip={}, filter={}]",
                stepExecution.getStepName(),
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                stepExecution.getSkipCount(),
                stepExecution.getFilterCount());
        return stepExecution.getExitStatus();
    }
}
