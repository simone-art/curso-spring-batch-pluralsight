package com.example.demospringbatchcourse;

import com.example.demospringbatchcourse.config.Constants;
import com.example.demospringbatchcourse.domain.PatientBatchLoaderApp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PatientBatchLoaderApp.class)
@ActiveProfiles("dev")
public class BatchJobConfigurationTest {

    @Autowired
    private Job job;

    //Código que testa trechos de código
    @Test
    public void test(){
        assertNotNull(job);
        Assert.assertEquals(Constants.JOB_NAME, job.getName());
    }



}
