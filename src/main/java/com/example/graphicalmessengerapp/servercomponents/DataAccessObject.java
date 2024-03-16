package com.example.graphicalmessengerapp.servercomponents;

import java.io.IOException;
import java.sql.*;

public class DataAccessObject {
    Connection connection;
    PreparedStatement psGetAllUsers;
    PreparedStatement psGetUser;
    PreparedStatement psInsert;

    public DataAccessObject() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/messengerdatabase",
                    "root", "toor");

            psGetAllUsers = connection.prepareStatement("SELECT * FROM users");
            psGetUser = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            psInsert = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?,?)");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getUsers() {
        try {
            ResultSet results = psGetAllUsers.executeQuery();
            while (results.next()) {
                System.out.println(results.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String createUser(String user, String pass) {
        ResultSet results;
        try {
            psGetUser.setString(1, user);
            results = psGetUser.executeQuery();

            if (results.isBeforeFirst()) { //User already exists
                System.out.println("User already exists");
                return "User Already Exists";
            }
            if (!validatePassword(pass)) { // Password is invalid
                System.out.println("Invalid Password");
                return "Invalid Password";
            }

            psInsert.setString(1,user);
            psInsert.setString(2,pass);
            psInsert.executeUpdate();
            System.out.println("User Inserted");
            return "Valid";

        } catch (SQLException e) {
            e.printStackTrace();
            return "Invalid";
        }
    }

    public String validateUser(String user,String pass) {
        ResultSet results;
        String password;
        try {
            psGetUser.setString(1,user);
            results = psGetUser.executeQuery();

            if(!results.isBeforeFirst()) { // Username doesnt exist
                System.out.println("This username (" + user + ") does not exist");
                return "Invalid Username";
            }

            results.next(); // Get Password
            password = results.getString("password");

            if (!password.equals(pass)) { //Incorrect Password
                System.out.println("Incorrect Password");
                return "Incorrect Password";
            }

            return "Valid";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Invalid";
        }
    }

    private boolean validatePassword(String pass) {
        return ((pass != null) && (pass.length() >= 4));
    }
}
