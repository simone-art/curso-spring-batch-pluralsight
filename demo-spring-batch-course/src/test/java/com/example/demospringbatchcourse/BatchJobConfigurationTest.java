package com.example.demospringbatchcourse;

import com.example.demospringbatchcourse.config.Constants;
import com.example.demospringbatchcourse.domain.PatientBatchLoaderApp;
import com.example.demospringbatchcourse.domain.PatientRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PatientBatchLoaderApp.class)
@ActiveProfiles("dev")
public class BatchJobConfigurationTest {

    @Autowired
    private Job job;

    //Adicionado o FlatFileItemReader creado noBatchJobConfiguration
    @Autowired
    private FlatFileItemReader<PatientRecord> reader;

    private JobParameters jobParameters;

    @Before
    public void setUp(){
        Map<String, JobParameter> params = new HashMap<>();
        //test-unit-testing.csv é um arquivo com um só registro pra testar
        params.put(Constants.JOB_PARAM_FILE_NAME, new JobParameter("test-unit-testing.csv"));
        jobParameters = new JobParameters(params);
    }

    //Código que testa trechos de código
    @Test
    public void testReader() throws Exception{
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters);
        int count = 0;
        try{
            count = StepScopeTestUtils.doInStepScope(stepExecution, () -> {
                int numPatients = 0;
                PatientRecord patient;
                try {
                 reader.open(stepExecution.getExecutionContext());
                 while((patient = reader.read()) != null){
                     assertNotNull(patient);
                     assertEquals("0", patient.getSourceId());
                     assertEquals("Ana", patient.getFirstName());
                     assertEquals("Pereira", patient.getLastName());
                     assertEquals("apereira69@gmail.com", patient.getEmail());
                     assertEquals("(805)334-53-87", patient.getPhoneNumber());
                     assertEquals("Rua Bernardo Pinto", patient.getStreet());
                     assertEquals("Vila Guilherme", patient.getCity());
                     assertEquals("Sao Paulo", patient.getState());
                     numPatients++;
                 }
                } finally {
                    try { reader.close(); } catch (Exception e) { fail(e.toString());}
                }
                return numPatients;
            });
        }catch (Exception e){
            fail(e.toString());
        }
        //assertNotNull(job);
        //Assert.assertEquals(Constants.JOB_NAME, job.getName());

        assertEquals(1, count);
    }

}
