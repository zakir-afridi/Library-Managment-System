package com.lib.Controller;

import com.lib.Buttons.AddMemberSaveButton;
import com.lib.Function.ConnectionTest;
import com.lib.Function.DatabaseFunction;
import com.lib.Function.Function;
import com.lib.Function.RetriveImage;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.UnaryOperator;

public class UpdateMemberController {

    @FXML private StackPane root;
    @FXML private ImageView backgroundImage;
    @FXML private StackPane backgroundOverlay;
    @FXML private Label profLabel;
    @FXML public ImageView profileImage;
    @FXML public Button uploadButton;
    @FXML public Button clearButton1;
    @FXML public  Button searchMemberButton;
    @FXML public TextField memberIdField;
    @FXML public TextField idField;
    @FXML public TextField nameField;
    @FXML public TextField FNameField;
    @FXML public TextField phoneNoField;
    @FXML public TextField emailField;
    @FXML public TextArea addressArea;
    @FXML public TextField cnicField;
    @FXML public ComboBox<String> genderComboBox;
    @FXML private Button saveButton;
    @FXML private Button clearButton;

    private Image defaultImage;
    ConnectionTest c = new ConnectionTest();
    Function fun = new Function();

    @FXML
    private void initialize() {
        defaultImage = profileImage.getImage();
        setupDigitOnlyValidation();
        setupGenderComboBox();
        setupButtonActions();
        setupUploadButton();
        setupEnterKeyNavigation();
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
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupButtonActions() {
        clearButton.setOnAction(event -> clearForm());
        saveButton.setOnAction(event -> handleSave());
        searchMemberButton.setOnAction(event -> handleSearchButton(memberIdField.getText()));
        clearButton1.setOnAction(event -> handleDelete());
    }

    private void setupGenderComboBox() {
        genderComboBox.getItems().addAll("Male", "Female");
        genderComboBox.getSelectionModel().selectFirst();
    }

    private void setupDigitOnlyValidation() {
        UnaryOperator<TextFormatter.Change> digitFilter = change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d*") ? change : null;
        };

        phoneNoField.setTextFormatter(new TextFormatter<>(digitFilter));
        cnicField.setTextFormatter(new TextFormatter<>(digitFilter));

        phoneNoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 11) phoneNoField.setText(oldValue);
        });

        cnicField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 13) cnicField.setText(oldValue);
        });
    }

    public void clearForm() {
        memberIdField.clear();
        nameField.clear();
        FNameField.clear();
        phoneNoField.clear();
        emailField.clear();
        addressArea.clear();
        cnicField.clear();
        genderComboBox.getSelectionModel().selectFirst();
        profileImage.setImage(defaultImage);
    }

    private void handleSave() {
        if (validateInputs()) {
            new AddMemberSaveButton().updateMember(
                    Integer.parseInt(memberIdField.getText()),
                    nameField.getText(),
                    FNameField.getText(),
                    phoneNoField.getText(),
                    emailField.getText(),
                    cnicField.getText(),
                    addressArea.getText(),
                    genderComboBox.getValue(),
                    profileImage
            );
            showAlert(Alert.AlertType.INFORMATION, "Success", "Member updated successfully!");
            clearForm();
        }
    }

    private boolean validateInputs() {
        if (memberIdField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Member ID field cannot be empty for update.");
            return false;
        }
        if (nameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Name field cannot be empty");
            return false;
        }
        if (FNameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Father's name field cannot be empty");
            return false;
        }
        if (phoneNoField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Phone number field cannot be empty");
            return false;
        }
        if (emailField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Email field cannot be empty");
            return false;
        }
        if (cnicField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "CNIC field cannot be empty");
            return false;
        }
        if (phoneNoField.getText().length() != 11) {
            showAlert(Alert.AlertType.ERROR, "Error", "Phone number must be exactly 11 digits");
            return false;
        }
        if (cnicField.getText().length() != 13) {
            showAlert(Alert.AlertType.ERROR, "Error", "CNIC must be exactly 13 digits");
            return false;
        }
        if (!emailField.getText().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid email address");
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

    private void setupEnterKeyNavigation() {
        nameField.setOnAction(e -> FNameField.requestFocus());
        FNameField.setOnAction(e -> phoneNoField.requestFocus());
        phoneNoField.setOnAction(e -> cnicField.requestFocus());
        cnicField.setOnAction(e -> emailField.requestFocus());
        emailField.setOnAction(e -> genderComboBox.requestFocus());
        genderComboBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addressArea.requestFocus();
                event.consume();
            }
        });
        addressArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                saveButton.fire();
                event.consume();
            }
        });
    }

    public void handleSearchButton(String id) {

        if (id == null || id.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing ID", "Please enter a Member ID to search.");
            return;
        }

        memberIdField.setText(id);

        try (Connection con = c.getConnection();
             PreparedStatement pst = con.prepareStatement(
                     "SELECT name, fname, gender, contact, cnic, email, address, profile_pic FROM members WHERE std_id = ?")) {

            pst.setInt(1, Integer.parseInt(id));
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                FNameField.setText(rs.getString("fname"));
                genderComboBox.getSelectionModel().select(rs.getString("gender"));
                phoneNoField.setText(rs.getString("contact"));
                cnicField.setText(rs.getString("cnic"));
                emailField.setText(rs.getString("email"));
                addressArea.setText(rs.getString("address"));

                byte[] imageBytes = rs.getBytes("profile_pic");
                if (imageBytes != null && imageBytes.length > 0) {
                    Image image = new Image(new ByteArrayInputStream(imageBytes));
                    profileImage.setImage(image);
                } else {
                    profileImage.setImage(defaultImage);
                }

            } else {
                showAlert(Alert.AlertType.INFORMATION, "Not Found", "No member found with ID: " + id);
                clearForm();
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Member ID must be numeric.");
            e.printStackTrace();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while searching: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDelete(){
        String id = memberIdField.getText();

        if (id == null || id.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing ID", "Please enter a Member ID to delete.");
            return;
        }

        boolean confirmed = fun.alertConfirm("Confirm Deletion", "Are you sure you want to delete member with ID: " + id + "?");

        if (confirmed) {
            try (Connection con = c.getConnection();
                 PreparedStatement pst = con.prepareStatement("DELETE FROM members WHERE std_id = ?")) {

                pst.setInt(1, Integer.parseInt(id));
                int rowsAffected = pst.executeUpdate();

                if (rowsAffected > 0) {
                    fun.alertW("Success!", "Member deleted successfully!");
                    clearForm();
                } else {
                    fun.alertW("Information", "No member found with ID: " + id + " to delete.");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Member ID must be numeric.");
                e.printStackTrace();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred during deletion: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred during deletion: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
