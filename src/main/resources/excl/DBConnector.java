package com.impilsm.modelrecjdbc.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
    private Connection connection = null;

    public DBConnector() {
    }

    private final String URL = "jdbc:postgresql://172.20.255.193:5432/traffic2_center";
    private final String PASS = "2014traffic";
    private final String USER = "postgres";
    private final String DRIVER = "org.postgresql.Driver";

    private static final Logger logger = LoggerFactory.getLogger(DBConnector.class);


    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }


    public Connection getConnection() {
        try {
            Class.forName(DRIVER).newInstance();
            if (connection == null) {
                connection = DriverManager.getConnection(URL, USER, PASS);
            }
            System.out.println("Соединение установлено");
            return connection;
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }



}
