package com.lib.Function;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
public class ImageConverter {



        public static byte[] convertImageViewToByteArray(ImageView imageView) {
            try {
                Image fxImage = imageView.getImage();

                if (fxImage == null) {
                    System.out.println("ImageView does not contain any image.");
                    return null;
                }

                BufferedImage bImage = SwingFXUtils.fromFXImage(fxImage, null);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bImage, "png", baos);
                return baos.toByteArray();

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }


    public static void setImageFromDatabase(ImageView imageView, int id, Connection connection, String query) {
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                byte[] imageBytes = rs.getBytes(1);
                if (imageBytes != null) {
                    Image image = new Image(new ByteArrayInputStream(imageBytes));
                    imageView.setImage(image);
                    System.out.println("Image successfully loaded from database.");
                } else {
                    System.out.println("No image found for given ID.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
