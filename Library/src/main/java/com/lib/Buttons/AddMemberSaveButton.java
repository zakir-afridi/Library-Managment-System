package com.lib.Buttons;

import com.lib.Controller.AddMemberController;
import com.lib.Function.ConnectionTest;
import com.lib.Function.Function;
import com.lib.Function.ImageConverter;
import javafx.scene.image.ImageView;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddMemberSaveButton {
    private final ConnectionTest c = new ConnectionTest();
    private final ImageConverter converter = new ImageConverter();
    private final Function fun = new Function();
    AddMemberController m=new AddMemberController();

    public boolean saveMember(String name, String fName, String phoneNO,
                              String email, String cnic, String address,
                              String gender, ImageView img) {
        byte[] image = converter.convertImageViewToByteArray(img);

        try (Connection con = c.getConnection();
             PreparedStatement pst = con.prepareStatement(
                     "insert into members(name,fname,gender,contact,cnic,email,address,profile_pic) " +
                             "values(?,?,?,?,?,?,?,?)")) {

            pst.setString(1, name);
            pst.setString(2, fName);
            pst.setString(3, gender);
            pst.setString(4, phoneNO);
            pst.setString(5, cnic);
            pst.setString(6, email);
            pst.setString(7, address);
            pst.setBytes(8, image);

            if (pst.executeUpdate() > 0) {
                fun.alertW("Success", "Member saved successfully");


                return true;
            }
        } catch (Exception e) {
            fun.alertW("Error", "Failed to save member: " + e.getMessage());
        }
        return false;
    }
    public boolean updateMember(int memberId, String name, String fName, String phoneNO,
                                String email, String cnic, String address,
                                String gender, ImageView img) {
        byte[] image = converter.convertImageViewToByteArray(img);
        String q = "UPDATE members SET name=?, fname=?, gender=?, contact=?, cnic=?, email=?, address=?, profile_pic=? WHERE std_id=?";

        try (Connection con = c.getConnection();
             PreparedStatement pst = con.prepareStatement(q)) {

            pst.setString(1, name);
            pst.setString(2, fName);
            pst.setString(3, gender);
            pst.setString(4, phoneNO);
            pst.setString(5, cnic);
            pst.setString(6, email);
            pst.setString(7, address);
            pst.setBytes(8, image);
            pst.setInt(9, memberId);

            if (pst.executeUpdate() > 0) {
                fun.alertW("Success", "Member updated successfully.");
                return true;
            }
        } catch (Exception e) {
            fun.alertW("Error", "Failed to update member: " + e.getMessage());
        }
        return false;
    }

    public boolean updateBooks(int bookId, String name, String author_Name, String edition,
                               String quantity, ImageView img) {
        byte[] image = converter.convertImageViewToByteArray(img);

        String query = "UPDATE books SET book_name=?, author=?, edition=?, quantity=?, profile_pic=? WHERE book_id=?";

        try (Connection con = c.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, name);
            pst.setString(2, author_Name);
            pst.setString(3, edition);
            pst.setInt(4, Integer.parseInt(quantity));
            pst.setBytes(5, image);
            pst.setInt(6, bookId);

            if (pst.executeUpdate() > 0) {
                fun.alertW("Success", "Book updated successfully.");
                return true;
            }
        } catch (Exception e) {
            fun.alertW("Error", "Failed to update book: " + e.getMessage());
        }

        return false;
    }

}