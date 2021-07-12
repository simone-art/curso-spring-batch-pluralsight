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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PatientBatchLoaderApp.class)
@ActiveProfiles("dev")
public class BatchJobConfigurationTest {

    @Autowired
    private Job job;

    @Autowired
    private FlatFileItemReader<PatientRecord> reader;

    private JobParameters jobParameters;

    @Before
    public void setUp(){
        Map<String, JobParameter> params = new HashMap<>();
        params.put(Constants.JOB_PARAM_FILE_NAME, new JobParameter("test-unit-testing.csv"));
        jobParameters = new JobParameters(params);
    }

    //Código que testa trechos de código
    @Test
    public void test(){
        assertNotNull(job);
        Assert.assertEquals(Constants.JOB_NAME, job.getName());
    }



}
