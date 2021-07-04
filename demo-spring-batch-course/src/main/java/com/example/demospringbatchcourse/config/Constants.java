package com.example.demospringbatchcourse.config;

public final class Constants {

    //Conta usada por toda a database pra interatuar com o processor batch
    public static final String SYSTEM_ACCOUNT = "system";

    // Perfil pra ordar em modo development
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";

    // Perfil pra ordar em modo production
    public static final String SPRING_PROFILE_PRODUCTION = "prod";

    // Perfil so spring para não incluir liquidbase database schema managemente ao rodar
    public static final String SPRING_PROFILE_NO_LIQUIBASE = "no-liquidabse";

    public static final String JOB_NAME = "patient-batch-loader";

    public static final String JOB_PARAM_FILE_NAME = "patient-batch-loader.fileName";

    public static final String INPUT_PATCH = "C:/demo/patient-batch-loader/data";

    public static final String STEP_NAME = "process-patients_step";

    private Constants(){

    }

}
