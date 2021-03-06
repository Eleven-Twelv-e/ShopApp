package com.nicatmagerramov;

import com.nicatmagerramov.db.DBConnector;
import com.nicatmagerramov.users.User;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;

import static org.junit.Assert.assertTrue;

public class DBConnectorTest {
    private static DBConnector connector;

    @ClassRule
    public static PostgreSQLContainer postgres = new PostgreSQLContainer();

    @Before
    public void setUp() {
        final User postgreUser = new User(postgres.getUsername(), postgres.getPassword());
        connector = new DBConnector(postgres.getJdbcUrl(), postgreUser);
    }

    @Test
    public void testDBConnectorConstructor() {
        assertTrue(connector instanceof DBConnector);
    }

    @Test
    public void testChangeDBUrl() {
        connector.changeDBUrl("new url");
        assertTrue(connector.getDbUrl().equals("new url"));
    }

    @Test
    public void testChangeUsernameAndPasswd() {
        final User testUser = new User("newUser", "newUser");
        connector.changeUser(testUser);
        assertTrue(connector.getUser().getUserName().equals("newUser") && connector.getUser().getUserPasswd().equals("newUser"));
    }

    @Test
    public void testGetConnection() {
        assertTrue(connector.getConnection() instanceof Connection);
    }
}
