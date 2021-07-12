package com.example.demospringbatchcourse.config;

import com.example.demospringbatchcourse.domain.PatientEntity;
import com.example.demospringbatchcourse.domain.PatientRecord;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;


import javax.batch.api.chunk.ItemReader;
import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

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

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    @Qualifier(value="batchEntityManagerFactory")
    private EntityManagerFactory batchEntityManagerFactory;



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
                //Valida parâmetros
                .validator(validator())
                .start(step)
                .build();
    }

    @Bean
    public Step step(ItemReader<PatientRecord> itemReader,
                     Function<PatientRecord, PatientEntity> processor,
                     JpaItemWriter<PatientEntity> writer) throws Exception {
        return this.stepBuilderFactory
                .get(Constants.STEP_NAME)
                .<PatientRecord, PatientEntity>chunk(2)
                .reader(itemReader)
                .processor(processor)
                .writer(writer)
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
    @StepScope
    public FlatFileItemReader<PatientRecord> reader(
            @Value("#{jobParameters['" + Constants.JOB_PARAM_FILE_NAME + "']}")String fileName) {
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
    @StepScope
    public Function<PatientRecord, PatientEntity> processor() {
        return (patient) ->  {
            return new PatientEntity(
                    patient.getSourceId(),
                    patient.getFirstName(),
                    patient.getLastName(),
                    patient.getEmail(),
                    patient.getPhoneNumber(),
                    patient.getStreet(),
                    patient.getCity(),
                    patient.getState();
        });
    }

    @Bean
    @StepScope
    public JpaItemWriter<PatientEntity> writer() {
        JpaItemWriter<PatientEntity> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(batchEntityManagerFactory);
        return writer;
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

}