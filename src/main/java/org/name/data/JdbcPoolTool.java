package org.name.data;

import org.testng.Assert;
import org.testng.Reporter;

import java.sql.*;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class JdbcPoolTool {

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String connString = "jdbc:h2:~/test";

    //  Database credentials
    private static final String user = "sa";
    private static final String pwd = "";

    private static AtomicInteger INITIAL_CAPACITY = new AtomicInteger(50);
    private LinkedList<Connection> pool = new LinkedList<Connection>();

    public JdbcPoolTool() throws SQLException {
        //init();
        for (int i = 0; i < INITIAL_CAPACITY.get(); i++) {
            pool.add(DriverManager.getConnection(connString, user, pwd));
        }
    }

    public synchronized Connection getConnection() throws SQLException {
        if (pool.isEmpty()) {
            pool.add(DriverManager.getConnection(connString, user, pwd));
            INITIAL_CAPACITY.incrementAndGet();
        }
        INITIAL_CAPACITY.decrementAndGet();
        return pool.pop();
    }

    public void close() throws SQLException {
        for (int i = 0; i < INITIAL_CAPACITY.get(); i++) pool.get(i).close();
    }

    public synchronized void returnConnection(Connection connection) {
        pool.push(connection);
    }

    public void init() {
        Connection conn = null;
        Statement stmt = null;
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 2: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(connString, user, pwd);

            //STEP 3: Execute a query
            System.out.println("Creating table in given database...");
            stmt = conn.createStatement();
            String sql = "CREATE TABLE USERS " +
                    "(id INTEGER not NULL, " +
                    " NAME VARCHAR(255), " +
                    " EMAIL VARCHAR(255), " +
                    " GENDER VARCHAR(255), " +
                    " STATUS VARCHAR(255)," +
                    " PRIMARY KEY ( id ))";
            stmt.executeUpdate(sql);
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                String sql = "INSERT INTO USERS VALUES (4406, 'Tenali Ramakrishna','tenalis.ramakrishna@1934ce.com', 'male', 'active');" +
                        "INSERT INTO USERS VALUES ('Mistyped Gender','tenalis.ramakrishna@1934ce.com', 'malel', 'active')" +
                "INSERT INTO USERS VALUES ('Mistyped Email','tenalis.ramakrishn@a@1934ce.com', 'male', 'active')" +
                        "INSERT INTO USERS VALUES ('Mistyped Status','tenalis.ramakrishna@1934ce.com', 'male', 'activel')" +
                        "INSERT INTO USERS VALUES ('','tenalis.ramakrishna@1934ce.com', 'male', 'active')";
                stmt.executeUpdate(sql);
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public User getUser(String query) {
        User user = new User();
        try (Statement stmt = getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if(rs.next()){
                String name = rs.getString("name");
                String email = rs.getString("email");
                String gender = rs.getString("gender");
                String status = rs.getString("status");
                user = new User(email,name,gender,status);
            }
        } catch (SQLException e) {
            Reporter.log(e.getMessage());
            Assert.assertTrue(false);
        }
        return user;
    }
}
