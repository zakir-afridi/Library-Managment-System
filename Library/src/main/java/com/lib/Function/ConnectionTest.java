package com.lib.Function;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javafx.scene.control.Alert;

public class ConnectionTest {


    public static Connection getConnection() {
        String url = "jdbc:mysql://localhost:3306/library";
        String user = "root";
        String pass = "zxcv";

        try {
            Connection con = DriverManager.getConnection(url, user, pass);
            
            return con;
            
        } 
        
        catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Connection Error");
            alert.setHeaderText("Connection field");
            alert.setContentText(e.getMessage());
            alert.showAndWait();

            return null;
        }
    }

}
