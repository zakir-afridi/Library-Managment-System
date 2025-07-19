package com.lib.Buttons;

import com.lib.Controller.AddMemberController;
import com.lib.Function.ConnectionTest;
import com.lib.Function.Function;
import com.lib.Function.ImageConverter;
import javafx.scene.image.ImageView;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddBookSaveButton {
    private final ConnectionTest c = new ConnectionTest();
    private final ImageConverter converter = new ImageConverter();
    private final Function fun = new Function();
    AddMemberController m=new AddMemberController();

    public boolean saveMember(ImageView img,String name, String authorName, String edition,
                             int quantity) {
        byte[] image = converter.convertImageViewToByteArray(img);

        try (Connection con = c.getConnection();
             PreparedStatement pst = con.prepareStatement(
                     "insert into books(book_name,author,edition,quantity,profile_pic) " +
                             "values(?,?,?,?,?)")) {

            pst.setString(1, name);
            pst.setString(2, authorName);
            pst.setString(3, edition);
            pst.setInt(4, quantity);
            pst.setBytes(5, image);


            if (pst.executeUpdate() > 0) {
                fun.alertW("Success", "Book saved successfully");


                return true;
            }
        } catch (Exception e) {
            fun.alertW("Error", "Failed to save Book: " + e.getMessage());
        }
        return false;
    }
}