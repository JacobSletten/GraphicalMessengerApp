package com.example.graphicalmessengerapp.servercomponents;

import java.io.IOException;
import java.sql.*;

public class DataAccessObject {
    Connection connection;
    Statement statement;
    PreparedStatement psGetUser;
    PreparedStatement psCheckUserExists;
    PreparedStatement psInsert;


    public DataAccessObject() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/messengerdatabase",
                    "root", "toor");

            statement = connection.createStatement();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getUsers() {
        try {
            ResultSet results = statement.executeQuery("select * from users");
            while (results.next()) {
                System.out.println(results.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean createUser(String user, String pass) {
        ResultSet results;
        try {
            psCheckUserExists = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            psCheckUserExists.setString(1, user);
            results = psCheckUserExists.executeQuery();

            if (results.isBeforeFirst()) { //User already exists
                System.out.println("User already exists");
                return false;
            } else { //Create new user
                if (!validatePassword(pass)) {
                    System.out.println("Invalid Password");
                    return false;
                }
                psInsert = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?,?)");
                psInsert.setString(1,user);
                psInsert.setString(2,pass);
                psInsert.executeUpdate();
                System.out.println("User Inserted");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean validateUser(String user,String pass) {
        ResultSet results;
        String password;
        try {
            psGetUser = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            psGetUser.setString(1,user);
            results = psGetUser.executeQuery();
            if(results.isBeforeFirst()) { //The username exists
                results.next();
                password = results.getString("password");
                if (!password.equals(pass)) { return false; }
                return true;
            } else { //Username does not exist
                System.out.println("This username (" + user + ") does not exist");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean validatePassword(String pass) {
        return ((pass != null) && (pass.length() >= 4));
    }
}
