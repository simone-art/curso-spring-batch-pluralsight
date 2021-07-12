package com.example.demospringbatchcourse.config;

import com.example.demospringbatchcourse.domain.PatientRecord;
import org.hibernate.engine.jdbc.internal.JdbcCoordinatorImpl;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;


import javax.batch.api.chunk.ItemReader;
import javax.batch.api.chunk.ItemWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.wrapIfMissing;


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

    @Bean
    public Step step(ItemReader<PatientRecord> itemReader) throws Exception {
        return this.stepBuilderFactory
                .get(Constants.STEP_NAME)
                .<PatientRecord, PatientRecord>chunk(2)
                .reader(itemReader)
                .processor(processor())
                .writer(writer())
                .build();

    }

    @Bean
    @StepScope
    public FlatFileItemReader<PatientRecord> reader(
            @Value("#{jobParameters['" + Constants.JOB_PARAM_FILE_NAME + "']}") String fileName, JdbcCoordinatorImpl applicationProperties) {
        return new FlatFileItemReaderBuilder<PatientRecord>()
            .name(Constants.ITEM_READER_NAME)
            .resource(
                    new PathResource(
                            Paths.get(applicationProperties.getBatch().getInputPath() +
                                    File.separator + fileName)))
                    .linesToSkip(1)
                    .lineMapper(lineMapper())
                    .build();
            }
      @Bean
      public LineMapper<PatientRecord> lineMapper(){
          DefaultLineMapper<PatientRecord> mapper = new DefaultLineMapper<>();
          mapper.setFieldSetMapper((fieldSet -> new PatientRecord(
                  fieldSet.readString(0), fieldSet.readString(1),
                  fieldSet.readString(2), fieldSet.readString(3),
                  fieldSet.readString(4), fieldSet.readString(5),
                  fieldSet.readString(6), fieldSet.readString(7),
                  fieldSet.readString(8), fieldSet.readString(9),
                  fieldSet.readString(10), fieldSet.readString(11),
                  fieldSet.readString(12),
          mapper.setLineTokenizer(new DelimitedLineTokenizer());
          return mapper;
      }

      @Bean
      @StepScope
      public PassThroughItemProcessor<PatientRecord> processor(){
        return new PassThroughItemProcessor<>();
      }

    @Bean
    @StepScope
    public ItemWriter<PatientRecord> writer(){
        return new ItemWriter<PatientRecord>(){
            @Override
            public void writer(List<? extends PatientRecord> items) throws Exception{
                for (PatientRecord patientRecord : items){
                    System.err.println("Writing item: " + patientRecord.toString());
                }
            }
        };
    }
}