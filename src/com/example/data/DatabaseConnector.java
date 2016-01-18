package com.example.data;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import com.example.bewerberportal.BewerberportalUI;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;

public class DatabaseConnector {

	private static JDBCConnectionPool pool;
	
	public static JDBCConnectionPool getPool(){
		if(pool == null){
			try {
				Properties properties = new Properties();
				InputStream stream = BewerberportalUI.getCurrent().getClass().getResourceAsStream("databaseconnection.properties");
				properties.load(stream);
				stream.close();
				String User = properties.getProperty("User");
				String Password = properties.getProperty("Password");
				pool = new SimpleJDBCConnectionPool("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/go2dhbw", User, Password);
			} catch (SQLException | FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return pool;
	}
}
