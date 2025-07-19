package com.lib.Function;


import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFunction {
    ConnectionTest c = new ConnectionTest();
    Function fun = new Function();

    public int retrieveTotal(String query, String column) {
        Connection con = c.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        int total = 0;

        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();

            if (rs.next()) {
                total = rs.getInt(column);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close(); // Close connection if not using a connection pool
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return total;
    }


    public ArrayList<String> retrieveNames(String query, String column) {
        Connection con = c.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        ArrayList<String> names = new ArrayList<>();

        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                names.add(rs.getString(column));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return names;
    }


    public ArrayList<Integer> retrieveId(String query, String column) {
        Connection con = c.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        ArrayList<Integer> ids = new ArrayList<>();

        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                ids.add(rs.getInt(column));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ids;
    }




    public String[] totalName(String q,String column, int totel) {

        String[] bookName = new String[totel];
        Connection con = c.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

            try {
                ps = con.prepareStatement(q);
                rs = ps.executeQuery();

                for (int i = 0; i < bookName.length; i++) {
                    if (rs.next()) {
                        bookName[i] = rs.getString(column);

                    }
                }

            } catch (Exception e) {
                Alert alert = fun.alertW("Warring!", e.getMessage());

            }
        return bookName;
    }

    public ImageView[] getImagesFromDatabase(String quary,int totel) {
        int total = totel;
        ImageView[] bookPics = new ImageView[total];

        try (Connection con = c.getConnection();
             PreparedStatement pst = con.prepareStatement(quary);
             ResultSet rs = pst.executeQuery()) {

            for (int i = 0; i < total; i++) {
                if (rs.next()) {
                    byte[] imageBytes = rs.getBytes(1);

                    if (imageBytes != null) {
                        Image image = new Image(new ByteArrayInputStream(imageBytes));
                        ImageView imageView = new ImageView(image);
                        imageView.setFitWidth(150);
                        imageView.setFitHeight(200);
                        imageView.setPreserveRatio(true);

                        bookPics[i] = imageView;

                    } else {

                        bookPics[i] = null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookPics;
    }







}


