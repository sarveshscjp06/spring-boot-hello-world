/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demoadb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.pool.OracleDataSource;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author stripathi
 */
@RestController
public class HomeController {

//    static {
//        System.setProperty("oracle.net.tns_admin",
//                System.getProperty("user.dir") + File.separator + "Wallet_raagatechdb");
//    }
    // final static String DB_URL = "jdbc:oracle:thin:@myhost:1521/orclservicename";
    // Use the TNS Alias name along with the TNS_ADMIN - For ATP and ADW
    //final static String DB_URL = "jdbc:oracle:thin:@raagatechdb_tpurgent?TNS_ADMIN=/Wallet_raagatechdb";
    final static String DB_URL = "jdbc:oracle:thin:@(description= (retry_count=20)(retry_delay=3)(address=(protocol=tcps)(port=1521)(host=adb.ap-mumbai-1.oraclecloud.com))(connect_data=(service_name=ge9bf8133738252_raagatechdb_high.adb.oraclecloud.com))(security=(ssl_server_dn_match=yes)))";
    final static String DB_USER = "ADMIN";
    final static String DB_PASSWORD = "Sarvesh12345";
    final static String CONN_FACTORY_CLASS_NAME = "oracle.jdbc.pool.OracleDataSource";

    @GetMapping("/")
    public String home() throws SQLException {

//        Connection conn = null;
        Statement stmt = null;
        ResultSet result = null;
        String test_table_users = "";

        try {
            test_table_users = getOracleDataSource();
//            PoolDataSource dataSource = getUCPoolDataSource();
//
//            if (dataSource != null) {
//                // Get the database connection from UCP.
//                try ( Connection conn = dataSource.getConnection()) {
//                    System.out.println("Available connections after checkout: "
//                            + dataSource.getAvailableConnectionsCount());
//                    System.out.println("Borrowed connections after checkout: "
//                            + dataSource.getBorrowedConnectionsCount());
//                    // Perform a database operation
//                    //doSQLWork(conn);
//                } catch (SQLException e) {
//                    System.out.println("UCPSample - " + "SQLException occurred : "
//                            + e.getMessage());
//                }
//                System.out.println("Available connections after checkin: "
//                        + dataSource.getAvailableConnectionsCount());
//                System.out.println("Borrowed connections after checkin: "
//                        + dataSource.getBorrowedConnectionsCount());
//            }
//            conn = getDatabaseConnection();
//            String selectUser = "select * from test_table";
//            stmt = conn.createStatement();
//
//            result = stmt.executeQuery(selectUser);
//            while (result.next()) {
//                test_table_users += "    " + result.getString("name");
//            }
//
        } catch (Exception ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
//        finally {
//            if (conn != null) {
//                result.close();
//                stmt.close();
//                conn.close();
//            }
//        }
//        System.out.println(System.getProperty("oracle.net.tns_admin"));
        return "Hello World of ADB " + test_table_users;
    }

    protected Connection getDatabaseConnection() throws ClassNotFoundException, SQLException {

        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@raagatechdb_tpurgent?TNS_ADMIN=/Wallet_raagatechdb", "ADMIN", "Sarvesh12345");
        return connection;
    }

    public PoolDataSource getUCPoolDataSource() throws Exception {
        // Get the PoolDataSource for UCP
        PoolDataSource pds = PoolDataSourceFactory.getPoolDataSource();
        // Set the connection factory first before all other properties
        pds.setConnectionFactoryClassName(CONN_FACTORY_CLASS_NAME);
        pds.setURL(DB_URL);
        pds.setUser(DB_USER);
        pds.setPassword(DB_PASSWORD);
        pds.setConnectionPoolName("JDBC_UCP_POOL");

        // Default is 0. Set the initial number of connections to be created
        // when UCP is started.
        pds.setInitialPoolSize(5);

        // Default is 0. Set the minimum number of connections
        // that is maintained by UCP at runtime.
        pds.setMinPoolSize(5);

        // Default is Integer.MAX_VALUE (2147483647). Set the maximum number of
        // connections allowed on the connection pool.
        pds.setMaxPoolSize(20);

        // Default is 30secs. Set the frequency in seconds to enforce the timeout
        // properties. Applies to inactiveConnectionTimeout(int secs),
        // AbandonedConnectionTimeout(secs)& TimeToLiveConnectionTimeout(int secs).
        // Range of valid values is 0 to Integer.MAX_VALUE. .
        pds.setTimeoutCheckInterval(5);

        // Default is 0. Set the maximum time, in seconds, that a
        // connection remains available in the connection pool.
        pds.setInactiveConnectionTimeout(10);

        // Get the database connection from UCP.
        try ( Connection conn = pds.getConnection()) {
            System.out.println("Available connections after checkout: "
                    + pds.getAvailableConnectionsCount());
            System.out.println("Borrowed connections after checkout: "
                    + pds.getBorrowedConnectionsCount());
            // Perform a database operation
            doSQLWork(conn);
        } catch (SQLException e) {
            System.out.println("UCPSample - " + "SQLException occurred : "
                    + e.getMessage());
        }
        System.out.println("Available connections after checkin: "
                + pds.getAvailableConnectionsCount());
        System.out.println("Borrowed connections after checkin: "
                + pds.getBorrowedConnectionsCount());

        return pds;
    }

    public String getOracleDataSource() throws SQLException {
        
        var dbUserName = "could not establish the connection";
        Properties info = new Properties();
        info.put(OracleConnection.CONNECTION_PROPERTY_USER_NAME, DB_USER);
        info.put(OracleConnection.CONNECTION_PROPERTY_PASSWORD, DB_PASSWORD);
        info.put(OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, "20");

        var ods = new OracleDataSource();
        ods.setURL(DB_URL);
        ods.setConnectionProperties(info);

        // With AutoCloseable, the connection is closed automatically.
        try ( OracleConnection connection = (OracleConnection) ods.getConnection()) {
            // Get the JDBC driver name and version 
            DatabaseMetaData dbmd = connection.getMetaData();
            System.out.println("Driver Name: " + dbmd.getDriverName());
            System.out.println("Driver Version: " + dbmd.getDriverVersion());
            // Print some connection properties
            System.out.println("Default Row Prefetch Value is: "
                    + connection.getDefaultRowPrefetch());
            dbUserName = connection.getUserName();
            System.out.println("Database Username is: " + dbUserName);
            System.out.println();
            // Perform a database operation 
            printEmployees(connection);
        }
        return dbUserName;
    }

    /*
  * Displays first_name and last_name from the employees table.
     */
    public static void printEmployees(Connection connection) throws SQLException {
        // Statement and ResultSet are AutoCloseable and closed automatically. 
        try ( Statement statement = connection.createStatement()) {
            try ( ResultSet resultSet = statement
                    .executeQuery("select id, name from test_table")) {
                System.out.println("FIRST_NAME" + "  " + "LAST_NAME");
                System.out.println("---------------------");
                while (resultSet.next()) {
                    System.out.println(resultSet.getInt(1) + " "
                            + resultSet.getString(2) + " ");
                }
            }
        }
    }

    /*
    * Creates an EMP table and does an insert, update and select operations on
    * the new table created.
     */
    public static void doSQLWork(Connection conn) {
        try {
            conn.setAutoCommit(false);
            // Prepare a statement to execute the SQL Queries.
            Statement statement = conn.createStatement();
            // Create table EMP
            statement.executeUpdate("create table EMP(EMPLOYEEID NUMBER,"
                    + "EMPLOYEENAME VARCHAR2 (20))");
            System.out.println("New table EMP is created");
            // Insert some records into the table EMP
            statement.executeUpdate("insert into EMP values(1, 'Jennifer Jones')");
            statement.executeUpdate("insert into EMP values(2, 'Alex Debouir')");
            System.out.println("Two records are inserted.");

            // Update a record on EMP table.
            statement.executeUpdate("update EMP set EMPLOYEENAME='Alex Deborie'"
                    + " where EMPLOYEEID=2");
            System.out.println("One record is updated.");

            // Verify the table EMP
            ResultSet resultSet = statement.executeQuery("select * from EMP");
            System.out.println("\nNew table EMP contains:");
            System.out.println("EMPLOYEEID" + " " + "EMPLOYEENAME");
            System.out.println("--------------------------");
            while (resultSet.next()) {
                System.out.println(resultSet.getInt(1) + " " + resultSet.getString(2));
            }
            System.out.println("\nSuccessfully tested a connection from UCP");
        } catch (SQLException e) {
            System.out.println("UCPSample - "
                    + "doSQLWork()- SQLException occurred : " + e.getMessage());
        } finally {
            // Clean-up after everything
            try ( Statement statement = conn.createStatement()) {
                statement.execute("drop table EMP");
            } catch (SQLException e) {
                System.out.println("UCPSample - "
                        + "doSQLWork()- SQLException occurred : " + e.getMessage());
            }
        }
    }
}
