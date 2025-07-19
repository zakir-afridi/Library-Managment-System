package com.lib.Controller;

import com.lib.Buttons.AddBookSaveButton;
import com.lib.Buttons.AddMemberSaveButton;
import com.lib.Function.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class AddBookController  {



    @FXML private ImageView bookImage;
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField authorNameField;
    @FXML private TextField editionField;
    @FXML private TextField quantityField;
    @FXML private Label profLabel;
    @FXML private Button uploadButton;
    @FXML private Button clearButton;
    @FXML private Button saveButton;

    private Image defaultImage;

    @FXML
    private void initialize() {
        defaultImage = bookImage.getImage();
        setupDigitOnlyValidation();
        setupButtonActions();
        setupUploadButton();
        enterButton();


    }

    private void setupUploadButton() {
        uploadButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Profile Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
            if (selectedFile != null) {
                try {
                    Image image = new Image(selectedFile.toURI().toString());
                    bookImage.setImage(image);
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not load the image: " + e.getMessage());
                }
            }
        });
    }

    private void setupButtonActions() {
        clearButton.setOnAction(event -> clearForm());
        saveButton.setOnAction(event -> handleSave());
    }



    private void setupDigitOnlyValidation() {
        UnaryOperator<TextFormatter.Change> digitFilter = change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d*") ? change : null;
        };

        quantityField.setTextFormatter(new TextFormatter<>(digitFilter));


        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 4) quantityField.setText(oldValue);
        });


    }

    public void clearForm() {
        nameField.clear();
        authorNameField.clear();
        editionField.clear();
        quantityField.clear();
        bookImage.setImage(defaultImage);
    }
    private final ImageConverter converter = new ImageConverter();

    private void handleSave() {
        if (validateInputs()) {
            new AddBookSaveButton().saveMember(
                    bookImage,
                    nameField.getText(),
                    authorNameField.getText(),
                    editionField.getText(),
                    Integer.parseInt(quantityField.getText())

            );
            clearForm();
        }
    }

    private boolean validateInputs() {
        if (nameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Name field cannot be empty");
            return false;
        }
        if (authorNameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Author's name field cannot be empty");
            return false;
        }
        if (editionField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Edition field cannot be empty");
            return false;
        }
        if (quantityField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Quantiy field cannot be empty");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void enterButton (){
        nameField.setOnAction(e -> authorNameField.requestFocus());
        authorNameField.setOnAction(e -> editionField.requestFocus());
        editionField.setOnAction(e -> quantityField.requestFocus());
        quantityField.setOnAction(e -> saveButton.fire());

    }
}