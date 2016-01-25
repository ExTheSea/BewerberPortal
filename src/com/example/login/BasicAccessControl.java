package com.example.login;

import java.sql.SQLException;

import com.example.bewerberportal.BewerberportalUI;
import com.example.data.DatabaseConnector;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;

/**
 * Default mock implementation of {@link AccessControl}. This implementation
 * accepts any string as a password, and considers the user "admin" as the only
 * administrator.
 */
public class BasicAccessControl implements AccessControl {

    @Override
    public boolean signIn(String username, String password) {
        if (username == null || username.isEmpty())
            return false;
        TableQuery tq_user = new TableQuery("benutzer", DatabaseConnector.getPool());
        SQLContainer cont = null;
        try {
			cont = new SQLContainer(tq_user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
        cont.addContainerFilter(new Like("email", username));
        Item item = cont.getItem(cont.firstItemId());
        if(item == null)
        	return false;
        if(item.getItemProperty("passwort").getValue().toString().equals(password)){
            CurrentUser.set(item.getItemProperty("id").getValue().toString());
            BewerberportalUI.getCurrent().login(Integer.valueOf(item.getItemProperty("account_id").getValue().toString()), username);
            return true;
        }
        return false;
    }

    @Override
    public boolean isUserSignedIn() {
        return !CurrentUser.get().isEmpty();
    }

    @Override
    public boolean isUserInRole(String role) {
        if ("admin".equals(role)) {
            // Only the "admin" user is in the "admin" role
            return getPrincipalName().equals("admin");
        }

        // All users are in all non-admin roles
        return true;
    }

    @Override
    public String getPrincipalName() {
        return CurrentUser.get();
    }

}