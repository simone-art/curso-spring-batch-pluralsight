package com.example.demospringbatchcourse.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

//@Component registra esta classe como um componente
// @EnableBatchProcessing habilita o processo batch
// e configura que esta classe se escanee com a nossa configuração

@Component
@EnableBatchProcessing
public class BatchConfiguration  {


    //JobRepository é responsável pela persistência dos dados feitos pelo batch jobs
    //Estes dados persistem em memória ou na database.
    // JobRepository é muito usado com database e tem que ser configurado usando TransactionManager e DataSource
    //JobExplorer configura os getters e proporciona métodos pra recuperar dados que estão no JobRepositiry
    //JobLauncher é responsável de executar jobs com um conjunto de parámetros definidos
    private JobRepository jobRepository;
    private JobExplorer jobExplorer;
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier(value = "batchTransactionManager")
    private PlatformTransactionManager batchTransactionManager;

    @Autowired
    @Qualifier (value = "batchDataSource")
    private DataSource batchDataSource;

    //Getters

    //@Override
    public JobRepository getJobRepository() throws Exception {
        return this.jobRepository;
    }

    public JobExplorer getJobExplorer() throws Exception{
        return this.jobExplorer;
    }

    public JobLauncher getJobLauncher() throws Exception {
        return this.jobLauncher;
    }

    public PlatformTransactionManager getBatchTransactionManager() {
        return this.batchTransactionManager;
    }

    //Configurando o JobLauncher
    protected JobLauncher createJobLauncher() throws Exception{
        //Inicializa o JobLauncher e só executa uma tarefa por pedido
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    //Configurando o JobRepository
    protected JobRepository createJobRepository() throws Exception{
        JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
        factoryBean.setDataSource(this.batchDataSource);
        factoryBean.setTransactionManager(getBatchTransactionManager());
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    //Configurando o JobRepository, JobExplorer e JobLauncher
    @PostConstruct
    public void afterPropertiesSet() throws Exception{
        this.jobRepository = createJobRepository();
        JobExplorerFactoryBean jobExplorerFactoryBean = new JobExplorerFactoryBean();
        jobExplorerFactoryBean.setDataSource(this.batchDataSource);
        jobExplorerFactoryBean.afterPropertiesSet();
        this.jobExplorer = jobExplorerFactoryBean.getObject();
        this.jobLauncher = createJobLauncher();
    }

}
