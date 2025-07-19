package com.lib.Controller;

import com.lib.Function.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class SettingsController {

    @FXML
    private ImageView bookImage;

    @FXML
    private Button uploadButton;

    @FXML
    private TextArea titleTextArea;

    @FXML
    private Button saveButton;

    @FXML
    private RadioButton backgroundRadio;

    @FXML
    private Label profLabel;

    private final ConnectionTest c = new ConnectionTest();
    private final Function fun = new Function();
    private final ImageConverter imageConverter = new ImageConverter();
    private final DeshboardRefresh dashboardRefresher = new DeshboardRefresh();
    private final RetriveLibraryDetails libraryDetails = new RetriveLibraryDetails();
    private final RetriveImage imageRetriever = new RetriveImage();

    @FXML
    private void initialize() {
        retrieveTitle();
        retrieveLogo();

        uploadButton.setOnAction(e -> {
            handleUploadButtonClick();


        });
        saveButton.setOnAction(e -> {
            handleSaveButtonClick();
            handleRadioChange();
        });
    }

    private void handleUploadButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose New Icon");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            bookImage.setImage(image);
        }
    }

    private void handleSaveButtonClick() {
        String newTitle = titleTextArea.getText();

        if (bookImage.getImage() == null) {
            fun.alertW("Warning", "Please upload an image before saving.");
            return;
        }

        byte[] img = imageConverter.convertImageViewToByteArray(bookImage);

        try (Connection con = c.getConnection()) {
            String q = "UPDATE librarydetails SET library_title = ?, library_logo = ? WHERE id = 1";
            PreparedStatement pst = con.prepareStatement(q);
            pst.setString(1, newTitle);
            pst.setBytes(2, img);

            if (pst.executeUpdate() > 0) {
                fun.alertW("Success", "Changes saved successfully.");
                Stage currentStage = (Stage) titleTextArea.getScene().getWindow();
                currentStage.close();
            }
        } catch (Exception e) {
            fun.alertW("Error", "Failed to save changes: " + e.getMessage());
        }
    }

    public void handleRadioChange() {
        if (backgroundRadio.isSelected()) {

        }
    }

    private void retrieveTitle() {
        titleTextArea.setText(libraryDetails.retriveTilte());
    }

    private void retrieveLogo() {
        ImageView img = imageRetriever.retriveImage("SELECT library_logo FROM librarydetails WHERE id = 1");
        if (img != null && img.getImage() != null) {
            bookImage.setImage(img.getImage());
        }
    }
}
