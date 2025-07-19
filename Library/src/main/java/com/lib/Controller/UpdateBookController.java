package com.lib.Controller;

import com.lib.Buttons.AddMemberSaveButton;
import com.lib.Function.ConnectionTest;
import com.lib.Function.Function;
import com.lib.Function.RetriveImage;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.UnaryOperator;

public class UpdateBookController {

    @FXML private StackPane root;
    @FXML private ImageView backgroundImage;
    @FXML private StackPane backgroundOverlay;
    @FXML private Label profLabel;
    @FXML public ImageView profileImage;
    @FXML public Button uploadButton;
    @FXML public Button clearButton1;
    @FXML public Button searchBookButton;
    @FXML public TextField bookIdField;
    @FXML public TextField idField;
    @FXML public TextField nameField;
    @FXML public TextField authorNameField;
    @FXML public TextField editionField;
    @FXML public TextField quantiyField;
    @FXML private Button saveButton;
    @FXML private Button clearButton;

    private Image defaultImage;

    ConnectionTest c = new ConnectionTest();
    RetriveImage r = new RetriveImage();
    Function fun = new Function();

    @FXML
    private void initialize() {
        defaultImage = profileImage.getImage();
        setupDigitOnlyValidation();
        setupButtonActions();
        setupUploadButton();
        setupFieldNavigation();
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
                    profileImage.setImage(image);
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not load the image: " + e.getMessage());
                }
            }
        });
    }

    private void setupButtonActions() {
        clearButton.setOnAction(event -> clearForm());
        saveButton.setOnAction(event -> handleSave());
        searchBookButton.setOnAction(event -> handleSearchBook(bookIdField.getText()));
        clearButton1.setOnAction(event -> handleDeleteBook());
    }

    private void setupDigitOnlyValidation() {
        UnaryOperator<TextFormatter.Change> digitFilter = change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d*") ? change : null;
        };

        quantiyField.setTextFormatter(new TextFormatter<>(digitFilter));
        bookIdField.setTextFormatter(new TextFormatter<>(digitFilter));
    }

    public void clearForm() {
        bookIdField.clear();
        nameField.clear();
        authorNameField.clear();
        editionField.clear();
        quantiyField.clear();
        profileImage.setImage(defaultImage);
    }

    private void handleSave() {
        if (validateInputs()) {
            new AddMemberSaveButton().updateBooks(
                    Integer.parseInt(bookIdField.getText()),
                    nameField.getText(),
                    authorNameField.getText(),
                    editionField.getText(),
                    quantiyField.getText(),
                    profileImage
            );
            clearForm();
        }
    }

    private boolean validateInputs() {
        if (nameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Name field cannot be empty.");
            return false;
        }
        if (authorNameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Author's name field cannot be empty.");
            return false;
        }
        if (editionField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Edition field cannot be empty.");
            return false;
        }
        if (quantiyField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Quantity field cannot be empty.");
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

    private void setupFieldNavigation() {
        nameField.setOnAction(e -> authorNameField.requestFocus());
        authorNameField.setOnAction(e -> editionField.requestFocus());
        editionField.setOnAction(e -> quantiyField.requestFocus());
        quantiyField.setOnAction(event -> saveButton.fire());
    }

    public void handleSearchBook(String id) {
        if (id == null || id.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing ID", "Please enter a Book ID to search.");
            return;
        }

        bookIdField.setText(id);

        try (Connection con = c.getConnection()) {
            String query = "SELECT book_name, author, edition, quantity, profile_pic FROM books WHERE book_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, Integer.parseInt(id));
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("book_name"));
                authorNameField.setText(rs.getString("author"));
                editionField.setText(rs.getString("edition"));
                quantiyField.setText(rs.getString("quantity"));

                byte[] imageBytes = rs.getBytes("profile_pic");
                if (imageBytes != null) {
                    Image image = new Image(new ByteArrayInputStream(imageBytes));
                    profileImage.setImage(image);
                } else {
                    profileImage.setImage(defaultImage);
                }

            } else {
                showAlert(Alert.AlertType.INFORMATION, "Not Found", "No book found with ID: " + id);
                clearForm();
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Book ID must be numeric.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeleteBook() {
        String id = bookIdField.getText();

        if (id == null || id.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing ID", "Please enter a Book ID to delete.");
            return;
        }

        boolean confirmed = fun.alertConfirm("Are you sure?", "Do you want to delete this book?");
        if (confirmed) {
            try (Connection con = c.getConnection()) {
                String query = "DELETE FROM books WHERE book_id = ?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setInt(1, Integer.parseInt(id));
                int rowsAffected = pst.executeUpdate();

                if (rowsAffected > 0) {
                    fun.alertW("Success", "Book deleted successfully.");
                    clearForm();
                } else {
                    fun.alertW("Info", "No book found with ID: " + id);
                }
            } catch (SQLException e) {
                fun.alertW("Database Error", e.getMessage());
            }
        }
    }
}
