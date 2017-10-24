package com.standartproect.isodsecurity.connection;

public class Configuration {

    public String DB_USER_NAME ;

    public String DB_PASSWORD ;

    public String DB_URL;

    public String DB_DRIVER;

    public Integer DB_MAX_CONNECTIONS;


    public Configuration(){
        init();
    }

    private static Configuration configuration = new Configuration();

    public static Configuration getInstance(){
        return configuration;
    }

    private void init(){
        DB_USER_NAME = "postgres";
        DB_PASSWORD = "2014traffic";
        DB_URL = "jdbc:postgresql://172.20.255.193:5432/traffic2_center";
        DB_DRIVER = "org.postgresql.Driver";
        DB_MAX_CONNECTIONS = 15;
    }
}
