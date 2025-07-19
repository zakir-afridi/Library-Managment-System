package com.lib.Controller;

import com.lib.Function.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class IssueBookController {

    @FXML
    private StackPane root;
    @FXML
    private ImageView backgroundImage;
    @FXML
    private StackPane backgroundOverlay;
    @FXML
    private ImageView memberImageView;  // Changed to more descriptive name
    @FXML
    private ImageView bookImageView;   // Changed to more descriptive name
    @FXML
    private TextField memberNameField;
    @FXML
    private TextField bookNameField;
    @FXML
    private TextField bookIdField;
    @FXML
    private TextField memberIdField;
    @FXML
    private TextField issueDateField;
    @FXML
    private DatePicker returnDatePicker; // Changed to more descriptive name
    @FXML
    private Button issueButton;
    @FXML
    private Button clearButton;

    private final ConnectionTest c = new ConnectionTest();
    private final Function fun = new Function();
    private final DatabaseFunction d = new DatabaseFunction();
    private final RetriveImage r = new RetriveImage();
    private final DeshboardRefresh desh=new DeshboardRefresh();

    @FXML
    public void initialize() {

        enterButton();

        issueDateField.setText(LocalDate.now().toString());


        issueButton.setOnAction(e -> handleIssueButton());
        clearButton.setOnAction(e -> handleClearButton());
    }

    final ImageView bookImage=bookImageView;
    final ImageView memImage=memberImageView;

    @FXML
    private void handleIssueButton() {
        issueButton();

    }

    @FXML
    private void handleClearButton() {
        bookIdField.clear();
        memberIdField.clear();
        memberNameField.clear();
        bookNameField.clear();
        returnDatePicker.setValue(null);
    }


    public void issueButton() {


        if (bookIdField.getText().isEmpty()
                || memberIdField.getText().isEmpty()
                || issueDateField.getText().isEmpty()) {
            fun.alertW("waring!", "Please Fill all the field before Issue");

        } else {
            try {
                Connection con = c.getConnection();
                String q = "INSERT INTO issue_books(std_id, book_id, issue_date, return_date) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = con.prepareStatement(q);
                pst.setInt(1, Integer.parseInt(memberIdField.getText()));
                pst.setInt(2, Integer.parseInt(bookIdField.getText()));
                pst.setString(3, issueDateField.getText());
                pst.setString(4, (returnDatePicker.getValue() != null) ? returnDatePicker.getValue().toString() : null);







                boolean confirmed = fun.alertConfirm("Are you sure?", "Do you  want to issue this book");
                if (confirmed) {
                    if (pst.executeUpdate() > 0) {
                        fun.alertW("Success", "Book Issue successfully");
                        bookUpadate(Integer.parseInt(bookIdField.getText()));
                        Stage currentStage = (Stage) memberNameField.getScene().getWindow();
                        currentStage.close();

                    }
                } else {

                }

            } catch (Exception e) {
                fun.alertW("Warning!", e.getMessage());
            }


        }
    }




    public  ImageView retriveMemberImage(){
        ImageView[] img =d.getImagesFromDatabase("select profile_pic from members where std_id='"+ memberIdField.getText()+"'",1);
        ImageView imge=img[0];
        if (img!=null){
            return imge;}
        else
            return memImage;

    }
    public  ImageView retriveBookImage(){
        ImageView[] img =d.getImagesFromDatabase("select profile_pic from books where book_id='"+ bookIdField.getText()+"'",1);
        ImageView imge=img[0];
        if (img!=null){
            return imge;}
        else
            return bookImage;

    }
    public String retriveMemberName() {
        String text = memberIdField.getText();
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        return nameRetrive("SELECT name FROM members WHERE std_id=?", Integer.parseInt(text), "name");
    }

    public String retriveBookName() {
        String text = bookIdField.getText();
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        return nameRetrive("SELECT book_name FROM books WHERE book_id=?", Integer.parseInt(text), "book_name");
    }



    private void enterButton (){
        bookIdField.setOnAction(e -> {
            memberIdField.requestFocus();
            ImageView img = r.retriveImage("SELECT profile_pic FROM books WHERE book_id = '"+bookIdField.getText()+"'");
            if (img != null && img.getImage() != null) {
                bookImageView.setImage(img.getImage());


            }
            bookNameField.clear();
            bookNameField.setText(retriveBookName());
        });

        memberIdField.setOnAction(e -> {

            returnDatePicker.requestFocus();
            ImageView imge = r.retriveImage("SELECT profile_pic  FROM members where std_id  = '"+memberIdField.getText()+"'");
            if (imge != null && imge.getImage() != null) {
                memberImageView.setImage(imge.getImage());
            }

            memberNameField.clear();
            memberNameField.setText(retriveMemberName());
        });



    }

    public String nameRetrive(String query, int id, String columnName) {
        String name = "";

        try (Connection con = c.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                name = rs.getString(columnName);
            }

        } catch (Exception e) {
            fun.alertW("Error", "Failed to retrieve name: " + e.getMessage());
        }

        return name;
    }

    private void bookUpadate(int id){
        Connection con=c.getConnection();
        try {
            String q="update books set quantity=quantity-1 where book_id=?;";
            PreparedStatement pst=con.prepareStatement(q);
            pst.setInt(1,id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}