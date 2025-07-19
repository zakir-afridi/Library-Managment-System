package com.lib.Controller;

import com.lib.Buttons.AddMemberSaveButton;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.function.UnaryOperator;

public class AddMemberController {

    @FXML private StackPane root;
    @FXML private ImageView backgroundImage;
    @FXML private StackPane backgroundOverlay;
    @FXML private Label profLabel;
    @FXML public ImageView profileImage;
    @FXML public Button uploadButton;
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

    @FXML
    private void initialize() {
        defaultImage = profileImage.getImage();
        setupDigitOnlyValidation();
        setupGenderComboBox();
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
            new AddMemberSaveButton().saveMember(
                    nameField.getText(),
                    FNameField.getText(),
                    phoneNoField.getText(),
                    emailField.getText(),
                    cnicField.getText(),
                    addressArea.getText(),
                    genderComboBox.getValue(),
                    profileImage
            );
            clearForm();
        }
    }

    private boolean validateInputs() {
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

    private  void enterButton(){

        nameField.setOnAction(e -> FNameField.requestFocus());
        FNameField.setOnAction(e -> phoneNoField.requestFocus());
        phoneNoField.setOnAction(e -> cnicField.requestFocus());
        cnicField.setOnAction(e -> emailField.requestFocus());
        emailField.setOnAction(e -> genderComboBox.requestFocus());
        genderComboBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                    addressArea.requestFocus();
            }
        });
        addressArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                saveButton.fire();
            }
        });

    }
}