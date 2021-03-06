package com.nicatmagerramov;

import com.nicatmagerramov.db.DBCursorHolder;
import com.nicatmagerramov.db.DBUtils;
import com.nicatmagerramov.exceptions.BasketException;
import com.nicatmagerramov.products.Product;
import com.nicatmagerramov.users.Client;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.SQLException;
import java.sql.Statement;

import static org.rnorth.visibleassertions.VisibleAssertions.assertEquals;

public class ClientTest {
    private HikariDataSource dataSource;

    @ClassRule
    public static PostgreSQLContainer postgres = new PostgreSQLContainer();

    @After
    public void closeDataSource() {
        dataSource.close();
    }

    @Before
    public void setUp() throws SQLException {
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(postgres.getJdbcUrl());
        hikariConfig.setUsername(postgres.getUsername());
        hikariConfig.setPassword(postgres.getPassword());

        dataSource = new HikariDataSource(hikariConfig);
        final Statement statement = dataSource.getConnection().createStatement();

        statement.addBatch("CREATE TABLE IF NOT EXISTS users ( user_name text PRIMARY KEY, user_passwd text NOT NULL," +
                " privileges boolean DEFAULT FALSE )");
        statement.addBatch("INSERT INTO users VALUES ( 'client', 'client', FALSE )");

        statement.addBatch(" CREATE TABLE IF NOT EXISTS baskets ( basket_id serial PRIMARY KEY," +
                " basket_owner text REFERENCES users(user_name) ON DELETE CASCADE, products_name text NOT NULL," +
                " products_amount text NOT NULL, processed boolean DEFAULT FALSE, created_at timestamp DEFAULT CURRENT_TIMESTAMP )");

        statement.addBatch("CREATE TABLE IF NOT EXISTS orders ( order_id serial, basket_id int4 REFERENCES baskets(basket_id) ON DELETE CASCADE," +
                " order_owner text REFERENCES users(user_name) ON DELETE CASCADE, address text NOT NULL, created_at timestamp DEFAULT CURRENT_TIMESTAMP )");

        statement.executeBatch();
        statement.close();
    }

    @Test
    public void testClientMethods() throws BasketException, SQLException {
        final Client client = new Client("client", "client");
        final Basket basket = new Basket();
        final Product apple = new Product("apple", 0.150, 0.8);

        // TESTING addProductToBasket
        client.addProductToBasket(basket, apple, 2);
        assertEquals("addProductToBasket succeeds", true, basket.getProducts().containsKey(apple));

        // TESTING removeProductFromBasket
        client.removeProductFromBasket(basket, apple, 2);
        assertEquals("removeProductFromBasket succeeds", 0, basket.getProducts().get(apple));

        // TESTING saveBasket
        client.addProductToBasket(basket, apple, 2);
        client.saveBasket(dataSource.getConnection(), basket);
        DBCursorHolder cursor = DBUtils.filterFromTable(dataSource.getConnection(), "baskets", new String[]{"basket_id"},
                new String[]{String.format("basket_owner = '%s'", client.getUserName())});
        cursor.getResults().next();

        final String resultSaveBasket = cursor.getResults().getString(1);
        cursor.closeCursor();
        assertEquals("saveBasket succeeds", "1", resultSaveBasket);

        // TESTING restoreBasket
        final Basket restoredBasket = client.restoreBasket(dataSource.getConnection());
        assertEquals("restoreBasket succeeds", basket.toString(), restoredBasket.toString());

        // TESTING completeOrder
        client.setRetrievedBasketId(dataSource.getConnection());
        client.completeOrder(dataSource.getConnection(), "London");
        cursor = DBUtils.filterFromTable(dataSource.getConnection(), "orders", new String[]{"order_id"},
                new String[]{String.format("order_owner = '%s'", client.getUserName())});
        cursor.getResults().next();

        final String resultCompleteOrder = cursor.getResults().getString(1);
        cursor.closeCursor();
        assertEquals("completeOrder - order update - succeeds", "1", resultCompleteOrder);

        cursor = DBUtils.filterFromTable(dataSource.getConnection(), "baskets", new String[]{"basket_id"},
                new String[]{String.format("basket_owner = '%s'", client.getUserName()), "AND", "processed = TRUE"});
        cursor.getResults().next();

        final String resultCompleteBasket = cursor.getResults().getString(1);
        cursor.closeCursor();
        assertEquals("completeOrder - basket update - succeeds", "1", resultCompleteBasket);
    }
}
