package com.nicatmagerramov.users;

import com.nicatmagerramov.Basket;
import com.nicatmagerramov.db.DBCursorHolder;
import com.nicatmagerramov.db.DBUtils;
import com.nicatmagerramov.interfaces.UserInterface;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User implements UserInterface {
    private String userName;
    private String userPasswd;
    private Basket bakset;

    public User(final String name, final String passwd) {
        this.userName = name;
        this.userPasswd = passwd;
    }

    @Override
    public DBCursorHolder fetchOrders(final Connection connection, final String[] filterArguments) throws SQLException {
        // Does user have privilege?
        DBCursorHolder cursor = DBUtils.filterFromTable(connection, "users", new String[]{"privileges"},
                new String[]{String.format("user_name = '%s'", userName)});
        cursor.getResults().next();

        final boolean userPrivilege = cursor.getResults().getBoolean(1);
        cursor.closeCursor();

        if (userPrivilege) {
            cursor = DBUtils.innerJoinTables(connection, "baskets", "orders", "basket_id",
                    new String[]{"products_name", "products_amount", "address"}, filterArguments);
            return cursor;
        } else {
            final List<String> nameAndFilterArguments = new ArrayList<>();
            nameAndFilterArguments.add(String.format("order_owner = '%s'", userName));
            if (filterArguments.length != 0) {
                nameAndFilterArguments.add("AND");
            }
            nameAndFilterArguments.addAll(Arrays.asList(filterArguments));
            cursor = DBUtils.innerJoinTables(connection, "baskets", "orders", "basket_id",
                    new String[]{"products_name", "products_amount", "address"}, nameAndFilterArguments.toArray(new String[0]));
            return cursor;
        }
    }

    @Override
    public DBCursorHolder fetchInventory(final Connection connection, final String[] filterArguments) throws SQLException {
        return DBUtils.innerJoinTables(connection, "products", "inventory", "product_id", new String[]{"product_id", "product_name", "product_weight", "product_price", "product_amount"},
                filterArguments);
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPasswd() {
        return userPasswd;
    }

    public void setBasket(final Basket basket) {
        this.bakset = basket;
    }

    public Basket getBasket() {
        return bakset;
    }
}
