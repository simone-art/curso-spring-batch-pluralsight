package com.example.demospringbatchcourse.config;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.commons.lang3.StringUtils.isBlank;


@Configuration
public class BatchJobConfiguration {

    //JobBuilderFactory proporciona o DSL pra configurar o Job
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    //Configura o step que o Job vai executar
    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Bean
    JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        postProcessor.setJobRegistry(jobRegistry);
        return postProcessor;
    }

    @Bean
    public Job job(Step step) throws Exception {
        return this.jobBuilderFactory
                .get(Constants.JOB_NAME)
                .incrementer(new RunIdIncrementer())
                //Valida parâmetros
                .validator(validator())
                .flow(step)
                .end()
                .build();
    }

    //Código que valida que os parâmetros definidos sejam validados
    public JobParametersValidator validator() {
        return new JobParametersValidator() {
            @Override
            public void validate(JobParameters parameters) throws JobParametersInvalidException {
                String fileName = parameters.getString(Constants.JOB_PARAM_FILE_NAME);
                if (isBlank(fileName)) {
                    throw new JobParametersInvalidException
                            ("The patient_batch_loader.filename parameter is required");
                }
                try {
                    Path file = Paths.get(Constants.INPUT_PATCH +
                            File.separator + fileName);
                    if (Files.notExists(file) || Files.isReadable(file)) {
                        throw new Exception("File did not exists or was not readable");
                    }
                } catch (Exception e) {
                    throw new JobParametersInvalidException(
                            "The input patch + patient_batch_loader.fileName parameters needs to " +
                                    "be a valid file location."
                    );
                }
            }
        };
    }

    public Step step() throws Exception {
        return this.stepBuilderFactory
                .get(Constants.STEP_NAME)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
                            throws Exception {
                        System.err.println("Hello World");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }
}