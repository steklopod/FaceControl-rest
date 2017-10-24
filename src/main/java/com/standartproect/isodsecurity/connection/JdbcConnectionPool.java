package com.standartproect.isodsecurity.connection;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class JdbcConnectionPool {

    List<Connection> availableConnections = new ArrayList<Connection>();

    private Connection createNewConnectionForPool()
    {
        Configuration config = Configuration.getInstance();
        try {
            Class.forName(config.DB_DRIVER);
            Connection connection = DriverManager.getConnection(
                    config.DB_URL, config.DB_USER_NAME, config.DB_PASSWORD);
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Connection getConnectionFromPool(){
        Connection connection = null;
        for (int i = 0; i < 2; ++i) {
            synchronized (this) {
                if (availableConnections.size() > 0) {
                    connection = availableConnections.remove(availableConnections.size() - 1);
                }
            }
            if (connection != null) {
                try {
                    connection.isValid(1000);
                    break;
                } catch (SQLException e) {
                    e.printStackTrace();
                    connection = null;
                }
            }
        }
        if (connection == null) {connection = createNewConnectionForPool();}
        return connection;
    }

    public synchronized void returnConnectionToPool(Connection connection)
    {
        availableConnections.add(connection);
    }


//    public JdbcConnectionPool(){initializeConnectionPool();}

//    private void initializeConnectionPool(){
//        while(!checkIfConnectionPoolIsFull()){ availableConnections.add(createNewConnectionForPool()); }
//    }
//    private synchronized boolean checkIfConnectionPoolIsFull()
//    {
//        final int MAX_POOL_SIZE = Configuration.getInstance().DB_MAX_CONNECTIONS;
//        if(availableConnections.size() < MAX_POOL_SIZE)
//        {
//            return false;
//        }
//        return true;
//    }
}