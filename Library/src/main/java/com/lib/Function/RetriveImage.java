package com.lib.Function;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RetriveImage {
    ConnectionTest c=new ConnectionTest();
    ImageView img = new ImageView();


    public ImageView  retriveImage(String quary)
    {

        try (
                Connection con = c.getConnection();
             PreparedStatement pst = con.prepareStatement(quary);
             ResultSet rs = pst.executeQuery()) {


                if (rs.next()) {
                    byte[] imageBytes = rs.getBytes(1);

                    if (imageBytes != null) {
                        Image image = new Image(new ByteArrayInputStream(imageBytes));
                        ImageView imageView = new ImageView(image);
                        imageView.setFitWidth(150);
                        imageView.setFitHeight(200);
                        imageView.setPreserveRatio(true);
                        img=imageView;

                    } else {

                        img= null;
                    }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return img;

    }
}
