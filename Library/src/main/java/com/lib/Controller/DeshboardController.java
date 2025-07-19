package com.lib.Controller;

import com.lib.Buttons.AddMemberSaveButton;
import com.lib.Function.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DeshboardController implements Initializable {

    DatabaseFunction retriveTotal = new DatabaseFunction();

    @FXML
    public StackPane mainStackPane;
    @FXML
    public Pane mainPane;

    @FXML
    public StackPane root;
    @FXML
    private HBox mainBox;
    @FXML
    private VBox leftVBox;
    @FXML
    private VBox rightVBox;
    @FXML
    private HBox titleHbox;
    public FlowPane flowPaneMember;
    @FXML
    public FlowPane flowPaneBook;
    @FXML
    public Label mainTitle;

    @FXML
    public StackPane backgroundOverlay;
    @FXML
    public VBox buttonVBox;

    @FXML
    private Label logo;
    @FXML
    protected Button addMemberButton;
    @FXML
    protected Button addBookButton;
    @FXML
    private Button issueBook;
    @FXML
    private Button returnBook;
    @FXML
    private Button updateMember;
    @FXML
    private Button updateBook;
    @FXML
    protected Button settings;
    @FXML
    private Button logout;
    @FXML
    public ImageView profileIcon;
    @FXML
    public ImageView profileIcon1;
    @FXML
    private AnchorPane AinB_anchor;
    @FXML
    public TabPane tabPane;
    @FXML
    private Tab reportTab;
    @FXML
    private Tab memberTab;
    @FXML
    private Tab bookTab;

    @FXML
    private StackPane reportTabStackPane;
    @FXML
    private StackPane memberTabStackPane;
    @FXML
    private StackPane bookTabStackPane;
    @FXML
    private GridPane reportGridPane;
    @FXML
    private ImageView backgroundImage;
    @FXML
    public Button MemberRefreshButton;
    @FXML
    private Button BookRefreshButton;
    @FXML
    public Button memberSearchButton;
    @FXML
    private TextField memberSearchField;
    @FXML
    private Button searchBookButton;
    @FXML private  ScrollPane scrollPaneMember;

    @FXML
    private HBox reportHBox;
    @FXML
    private VBox reportVBox;
    @FXML
    private HBox totalMemberHBox;
    @FXML
    private HBox toalBookHBox;
    @FXML
    private VBox totalIssueBookVBox;


    @FXML
    private TextField bookSearchField;

    @FXML
    private Button DeshboardRefreshButton;

    @FXML
    private PieChart piChart;

    @FXML
    private PieChart piChart2;


    @FXML
    private LineChart<String, Number> issueBooksLineChart;

    @FXML
    private Label totalMemberField;
    @FXML
    private Label totalBookField;
    @FXML
    private Label totalIssueBookField;


    @FXML
    DatabaseFunction d = new DatabaseFunction();
    RetriveLibraryDetails r = new RetriveLibraryDetails();
    ConnectionTest c = new ConnectionTest();
    Function fun = new Function();


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setupUIStyle();
        mainTitle.setText(r.retriveTilte());
        retrieveImage();
        loadBookImages("SELECT profile_pic FROM books ORDER BY book_id ASC", "SELECT book_id FROM books ORDER BY book_id ASC", "SELECT book_name FROM books ORDER BY book_id ASC");
        loadMemberImages("SELECT profile_pic FROM members ORDER BY std_id ASC", "SELECT std_id FROM members ORDER BY std_id ASC", "select name from members ORDER BY std_id ASC");
        setupButtonActions();
        setPiChart();
        setTopBooksChart();
        setIssueBooksLineChart();
        makeResponsive();

    }

    private void setupUIStyle() {
        buttonVBox.setStyle(styleButtonBox);
        mainBox.setStyle(styleSplit);
        tabPane.setStyle(styleforTab);
        mainTitle.setStyle(style);

        buttonStyle();


    }

    private void setupButtonActions() {
        BookRefreshButton.setOnAction(actionEvent -> {
            flowPaneBook.getChildren().clear();
            bookSearchField.clear();
            loadBookImages("SELECT profile_pic FROM books ORDER BY book_id ASC", "SELECT book_id FROM books ORDER BY book_id ASC", "SELECT book_name FROM books ORDER BY book_id ASC");
        });

        MemberRefreshButton.setOnAction(actionEvent -> {
            memberSearchField.clear();
            flowPaneMember.getChildren().clear();
            loadMemberImages("SELECT profile_pic FROM members ORDER BY std_id ASC", "SELECT std_id FROM members ORDER BY std_id ASC", "select name from members ORDER BY std_id ASC");
        });

        searchBookButton.setOnAction(actionEvent -> {
            String t = bookSearchField.getText().trim();
            if (t.isEmpty()) {
                flowPaneBook.getChildren().clear();
                loadBookImages("SELECT profile_pic FROM books ORDER BY book_id ASC", "SELECT book_id FROM books ORDER BY book_id ASC", "SELECT book_name FROM books ORDER BY book_id ASC");
            } else {
                String imageQuery = "SELECT profile_pic FROM books WHERE book_id = '" + t + "' OR book_name LIKE '%" + t + "%' ORDER BY book_id ASC";
                String idQuery = "SELECT book_id FROM books WHERE book_id = '" + t + "' OR book_name LIKE '%" + t + "%' ORDER BY book_id ASC";
                String nameQuery = "SELECT book_name FROM books WHERE book_id = '" + t + "' OR book_name LIKE '%" + t + "%' ORDER BY book_id ASC";
                flowPaneBook.getChildren().clear();
                loadBookImages(imageQuery, idQuery, nameQuery);
            }
        });

        memberSearchButton.setOnAction(actionEvent -> {
            String t = memberSearchField.getText().trim();
            if (t.isEmpty()) {
                flowPaneMember.getChildren().clear();
                loadMemberImages("SELECT profile_pic FROM members ORDER BY std_id ASC", "SELECT std_id FROM members ORDER BY std_id ASC", "select name from members ORDER BY std_id ASC");
            } else {
                String imageQuery = "SELECT profile_pic FROM members WHERE std_id = '" + t + "' OR name LIKE '%" + t + "%' ORDER BY std_id ASC";
                String idQuery = "SELECT std_id FROM members WHERE std_id = '" + t + "' OR name LIKE '%" + t + "%' ORDER BY std_id ASC";
                String nameQuery = "SELECT name FROM members WHERE std_id = '" + t + "' OR name LIKE '%" + t + "%' ORDER BY std_id ASC";
                flowPaneMember.getChildren().clear();
                loadMemberImages(imageQuery, idQuery, nameQuery);
            }
        });
        addMemberButton.setOnAction(event -> {

            addMemberLoad();
        });
        addBookButton.setOnAction(action -> {
            addBookLoad();
        });

        settings.setOnAction(event -> {
            settingsLoad();

        });

        issueBook.setOnAction(actionEvent -> {
            issueBookLoad();
        });
        returnBook.setOnAction(event -> returnBookLoad());
        updateBook.setOnAction(event -> updateBookLoad());
        updateMember.setOnAction(event -> updateMemberLoad());
        DeshboardRefresh d = new DeshboardRefresh();
        DeshboardRefreshButton.setOnAction(actionEvent -> {
            d.refresh(bookSearchField);
        });

        logout.setOnAction(eve -> {
            Stage currentStage = (Stage) mainTitle.getScene().getWindow();
            currentStage.close();
        });


    }

    public void loadBookImages(String imageQuery, String idQuery, String nameQuery) {
        flowPaneBook.getChildren().clear();
        ArrayList<Integer> bookIds = retriveTotal.retrieveId(idQuery, "book_id");
        ArrayList<String> bookNames = retriveTotal.retrieveNames(nameQuery, "book_name");
        ImageView[] bookImages = d.getImagesFromDatabase(imageQuery, bookIds.size());

        for (int i = 0; i < bookIds.size(); i++) {
            if (bookImages[i] != null) {
                ImageView imgView = bookImages[i];
                imgView.setUserData(bookIds.get(i));
                imgView.setCursor(Cursor.HAND);


                Label nameLabel = new Label("Name:" + bookNames.get(i) + "\nID: " + bookIds.get(i));

                nameLabel.setWrapText(true);
                nameLabel.setMaxWidth(100);
                nameLabel.setAlignment(Pos.CENTER);
                nameLabel.setTextFill(Color.WHITE);


                VBox container = new VBox(5, imgView, nameLabel);
                container.setStyle(styleforTab);
                container.setAlignment(Pos.CENTER);
                imgView.setOnMouseClicked(e -> {
                    int clickedStdId = (int) imgView.getUserData();

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lib/library/UpdateBook.fxml"));
                        Parent root = loader.load();
                        UpdateBookController controller = loader.getController();
                        controller.handleSearchBook(String.valueOf(clickedStdId));
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Update Member");
                        stage.setMaxWidth(950);
                        stage.setMaxHeight(650);
                        stage.setResizable(false);
                        stage.show();
                    } catch (IOException ex) {

                    }
                });
                flowPaneBook.getChildren().add(container);
            }
        }
    }

    private void loadMemberImages(String imageQuery, String idQuery, String nameQuery) {
        flowPaneMember.getChildren().clear();
        ArrayList<Integer> memberIds = retriveTotal.retrieveId(idQuery, "std_id");
        ArrayList<String> memberNames = retriveTotal.retrieveNames(nameQuery, "name");
        ImageView[] memberImages = d.getImagesFromDatabase(imageQuery, memberIds.size());
        totalMemberField.setText(String.valueOf(memberIds.size() ));

        for (int i = 0; i < memberIds.size(); i++) {
            if (memberImages[i] != null && i < memberNames.size()) {
                ImageView imgView = memberImages[i];
                imgView.setUserData(memberIds.get(i));
                imgView.setCursor(Cursor.HAND);


                Label nameLabel = new Label("Name:" + memberNames.get(i) + "\nID: " + memberIds.get(i));
                nameLabel.setWrapText(true);
                nameLabel.setMaxWidth(100);
                nameLabel.setAlignment(Pos.CENTER);
                nameLabel.setTextFill(Color.WHITE);
                nameLabel.getStyleClass().add("member-name");

                VBox container = new VBox(5, imgView, nameLabel);
                container.setStyle(styleforTab);
                container.setAlignment(Pos.CENTER);
                container.getStyleClass().add("image-container");
                imgView.setOnMouseClicked(e -> {
                    int clickedStdId = (int) imgView.getUserData();

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lib/library/UpdateMember.fxml"));
                        Parent root = loader.load();
                        UpdateMemberController controller = loader.getController();
                        controller.handleSearchButton(String.valueOf(clickedStdId));
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Update Member");

                        stage.setResizable(false);
                        stage.show();
                    } catch (IOException ex) {

                    }
                });
                flowPaneMember.getChildren().add(container);
            }
        }
    }

    public void retrieveImage() {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = c.getConnection();
            String query = "SELECT library_logo FROM libraryDetails WHERE id = 1";
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();

            if (rs.next()) {
                byte[] imageBytes = rs.getBytes("library_logo");

                if (imageBytes != null && imageBytes.length > 0) {
                    Image image = new Image(new ByteArrayInputStream(imageBytes));
                    Platform.runLater(() -> {
                        profileIcon1.setImage(image);
                        profileIcon1.setFitWidth(200);
                        profileIcon1.setFitHeight(200);
                        profileIcon1.setPreserveRatio(true);
                        profileIcon1.setSmooth(true);
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    String styleButtonBox = "-fx-background-color: linear-gradient(to bottom right,  #FF4500, #FF7F50, #FFD700); " +
            "-fx-background-radius: 5px; " +
            "-fx-border-width: 5px;" +
            "-fx-border-color: linear-gradient(to right, #51e2f5, #ffa8b6); " +
            "-fx-border-radius: 5px; " +
            "-fx-padding: 5px;";


    String style = "-fx-background-color: linear-gradient(to bottom right,ff0000, #FF4500, #FF7F50, #FFD700);" +
            "-fx-background-radius: 0; " +
            "-fx-border-width: 5px; " +
            "-fx-border-radius: 5px; " +
            "-fx-padding: 20px;";

    String styleforTab = "-fx-background-color: rgba(0,0,0,0.8); " +
            "-fx-border-width: 3px; " +
            "-fx-border-color: linear-gradient(to right, #4682B4, #6A5ACD); " +
            "-fx-border-radius: 0; " +
            "-fx-padding: 5px;";



    String styleSplit = """
            -fx-background-color: rgba(10,10,20,0.9);
            -fx-background-radius: 0;
            -fx-border-width: 5px;
            -fx-border-color: linear-gradient(to right, #ffa8B6, #55e7f7);
            -fx-border-radius: 0;
            -fx-padding: 10px;
            -fx-effect: innershadow(gaussian,#c81818, 15, 0.5, 0, 0),
                        dropshadow(gaussian, #ffa8B6, 15, 0.5, 0, 0);
            """;


    private void buttonStyle() {
        String s = "-fx-background-color:rgb(42,60,87); " +
                "-fx-background-radius: 0; " +
                "-fx-border-width: 2px; " +
                "-fx-border-color: linear-gradient(to right, #ffa8B6, #51e2f5); " +
                "-fx-border-radius: 5px; " +
                "-fx-padding: 5px;";
        searchBookButton.setStyle(s);
        MemberRefreshButton.setStyle(s);
        BookRefreshButton.setStyle(s);
        memberSearchButton.setStyle(s);
        DeshboardRefreshButton.setStyle(s);


    }

    public void addMemberLoad() {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lib/library/AddMember.fxml"));
            String css = getClass().getResource("/com/lib/library/CSS/AddMember.css").toExternalForm();
            Parent root = loader.load();
            root.getStylesheets().add(css);
            Stage newsStage = new Stage();
            newsStage.setResizable(false);
            newsStage.setScene(new Scene(root));
            newsStage.setTitle("Add Member");
            newsStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/lib/library/Photo/new_icon/ADD MEMBER.png")));

            newsStage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    public void addBookLoad() {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lib/library/AddBook.fxml"));
            String css = getClass().getResource("/com/lib/library/CSS/AddMember.css").toExternalForm();
            Parent root = loader.load();
            root.getStylesheets().add(css);
            Stage newsStage = new Stage();
            newsStage.setResizable(false);
            newsStage.setTitle("Add Book");
            newsStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/lib/library/Photo/new_icon/books.png")));

            newsStage.setScene(new Scene(root));
            newsStage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    public void settingsLoad() {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lib/library/Settings.fxml"));
            String css = getClass().getResource("/com/lib/library/CSS/AddMember.css").toExternalForm();
            Parent root = loader.load();
            root.getStylesheets().add(css);
            Stage newsStage = new Stage();
            newsStage.setResizable(false);
            newsStage.setTitle("Settings");
            newsStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/lib/library/Photo/new_icon/button_refresh.png")));

            newsStage.setScene(new Scene(root));
            newsStage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }


    public void issueBookLoad() {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lib/library/IssueBook.fxml"));
            String css = getClass().getResource("/com/lib/library/CSS/AddMember.css").toExternalForm();
            Parent root = loader.load();
            root.getStylesheets().add(css);
            Stage newsStage = new Stage();
            newsStage.setResizable(false);
            newsStage.setTitle("Issue Book");
            newsStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/lib/library/Photo/new_icon/books.png")));

            newsStage.setScene(new Scene(root));
            newsStage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    public void returnBookLoad() {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lib/library/ReturnBook.fxml"));
            String css = getClass().getResource("/com/lib/library/CSS/AddMember.css").toExternalForm();
            Parent root = loader.load();
            root.getStylesheets().add(css);
            Stage newsStage = new Stage();
            newsStage.setTitle("Return Book");
            newsStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/lib/library/Photo/new_icon/Return_icon.png")));

            newsStage.setTitle("Return Book");

            newsStage.setResizable(false);
            newsStage.setScene(new Scene(root));
            newsStage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    public void updateBookLoad() {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lib/library/UpdateBook.fxml"));
            String css = getClass().getResource("/com/lib/library/CSS/AddMember.css").toExternalForm();
            Parent root = loader.load();
            root.getStylesheets().add(css);
            Stage newsStage = new Stage();
            newsStage.setTitle("Update Book");
            newsStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/lib/library/Photo/new_icon/Books_icon.png")));

            newsStage.setResizable(false);
            newsStage.setScene(new Scene(root));
            newsStage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }


    public void updateMemberLoad() {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lib/library/UpdateMember.fxml"));
            String css = getClass().getResource("/com/lib/library/CSS/AddMember.css").toExternalForm();
            Parent root = loader.load();
            root.getStylesheets().add(css);
            Stage newsStage = new Stage();
            newsStage.setResizable(false);
            newsStage.setTitle("Update Member");
            newsStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/lib/library/Photo/new_icon/contactlist_theuser_802.png")));

            newsStage.setScene(new Scene(root));
            newsStage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    private void setPiChart() {
        String issuedQuery = "SELECT COUNT(*) AS total_issued FROM issue_books";
        String totalQuery = "SELECT SUM(quantity) AS total_quantity FROM books";

        int totalIssued = 0;
        int totalBooks = 0;

        try (Connection con = c.getConnection()) {

            try (PreparedStatement pst1 = con.prepareStatement(issuedQuery);
                 ResultSet rs1 = pst1.executeQuery()) {
                if (rs1.next()) {
                    totalIssued = rs1.getInt("total_issued");
                }
            }

            try (PreparedStatement pst2 = con.prepareStatement(totalQuery);
                 ResultSet rs2 = pst2.executeQuery()) {
                if (rs2.next()) {
                    totalBooks = rs2.getInt("total_quantity");
                }
            }

            int availableBooks = totalBooks - totalIssued;
            if (availableBooks < 0) availableBooks = 0;

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                    new PieChart.Data("Issued Books", totalIssued),
                    new PieChart.Data("Available Books", availableBooks)
            );

            piChart.setData(pieData);
            piChart.setTitle("Total Books Vs Issued Books");

            piChart.setLegendVisible(true);
            piChart.setLabelsVisible(true);

            totalBookField.setText(String.valueOf(totalBooks));

        } catch (SQLException e) {
            e.printStackTrace();
            fun.alertW("Error", "Could not load chart data: " + e.getMessage());
        }
    }

    public void setTopBooksChart() {
        String query = "SELECT book_name, quantity FROM books ORDER BY quantity DESC LIMIT 5";

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        try (Connection con = c.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String bookName = rs.getString("book_name");
                int quantity = rs.getInt("quantity");

                pieChartData.add(new PieChart.Data(bookName, quantity));
            }

            piChart2.setData(pieChartData);
            piChart2.setTitle("Top 5 Books by Quantity");
            piChart2.setLegendVisible(true);
            piChart2.setLabelsVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            fun.alertW("Error", "Could not load chart data: " + e.getMessage());
        }
    }

    public void setIssueBooksLineChart() {
        String query = "SELECT issue_date, COUNT(*) AS issued_count FROM issue_books " +
                "WHERE issue_date >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) " +
                "GROUP BY issue_date ORDER BY issue_date ASC";

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Books Issued in Last 30 Days");

        try (Connection con = c.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            int t = 0;
            while (rs.next()) {
                String date = rs.getString("issue_date");
                int count = rs.getInt("issued_count");
                t++;
                series.getData().add(new XYChart.Data<>(date, count));
            }

            totalIssueBookField.setText(String.valueOf(t));

            issueBooksLineChart.getData().clear();
            issueBooksLineChart.getData().add(series);

        } catch (SQLException e) {
            e.printStackTrace();
            fun.alertW("Error", "Could not load line chart data: " + e.getMessage());
        }
    }

    public void makeResponsive() {
        Stage stage= new Stage();
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setWidth(screenBounds.getWidth() * 1);
        stage.setHeight(screenBounds.getHeight() *1);
        stage.centerOnScreen();

        mainStackPane.prefWidthProperty().bind(stage.widthProperty());
        mainStackPane.prefHeightProperty().bind(stage.heightProperty());

        mainPane.prefWidthProperty().bind(mainStackPane.widthProperty());
        mainPane.prefHeightProperty().bind(mainStackPane.heightProperty());

        mainBox.prefWidthProperty().bind(mainPane.widthProperty());
        mainBox.prefHeightProperty().bind(mainPane.heightProperty());

        leftVBox.prefHeightProperty().bind(mainBox.heightProperty());
        leftVBox.prefWidthProperty().bind(mainBox.widthProperty().multiply(0.150));
        leftVBox.maxWidthProperty().bind(leftVBox.prefWidthProperty());
        leftVBox.minWidthProperty().bind(leftVBox.prefWidthProperty());

        rightVBox.prefWidthProperty().bind(mainBox.widthProperty().multiply(0.83));
        rightVBox.prefHeightProperty().bind(mainBox.heightProperty());

        titleHbox.prefWidthProperty().bind(rightVBox.widthProperty());
        final String mainTitleFontFamily = mainTitle.getFont() != null ? mainTitle.getFont().getFamily() : Font.getDefault().getFamily();
        mainTitle.fontProperty().bind(
                Bindings.createObjectBinding(() -> {
                    double newFontSize = stage.widthProperty().get() / 40.0;
                    return Font.font(mainTitleFontFamily, newFontSize);
                }, stage.widthProperty())
        );

        tabPane.prefWidthProperty().bind(rightVBox.widthProperty());
        tabPane.prefHeightProperty().bind(rightVBox.heightProperty().subtract(titleHbox.heightProperty()));

        reportTabStackPane.prefWidthProperty().bind(tabPane.widthProperty());
        reportTabStackPane.prefHeightProperty().bind(tabPane.heightProperty());
        memberTabStackPane.prefWidthProperty().bind(tabPane.widthProperty());
        memberTabStackPane.prefHeightProperty().bind(tabPane.heightProperty());
        bookTabStackPane.prefWidthProperty().bind(tabPane.widthProperty());
        bookTabStackPane.prefHeightProperty().bind(tabPane.heightProperty());

        reportGridPane.prefWidthProperty().bind(reportTabStackPane.widthProperty());
        reportGridPane.prefHeightProperty().bind(reportTabStackPane.heightProperty());
        reportGridPane.getColumnConstraints().forEach(cc -> cc.hgrowProperty().set(Priority.ALWAYS));
        reportGridPane.getRowConstraints().forEach(rc -> rc.vgrowProperty().set(Priority.ALWAYS));

        piChart.prefWidthProperty().bind(reportGridPane.widthProperty().divide(2));
        piChart.prefHeightProperty().bind(reportGridPane.heightProperty().divide(2));
        piChart2.prefWidthProperty().bind(reportGridPane.widthProperty().divide(2));
        piChart2.prefHeightProperty().bind(reportGridPane.heightProperty().divide(2));
        issueBooksLineChart.prefWidthProperty().bind(reportGridPane.widthProperty().divide(2));
        issueBooksLineChart.prefHeightProperty().bind(reportGridPane.heightProperty().divide(2));

        final String totalMemberFieldFontFamily = totalMemberField.getFont() != null ? totalMemberField.getFont().getFamily() : Font.getDefault().getFamily();
        totalMemberField.fontProperty().bind(
                Bindings.createObjectBinding(() -> {
                    double newFontSize = stage.widthProperty().get() / 35.0;
                    return Font.font(totalMemberFieldFontFamily, newFontSize);
                }, stage.widthProperty())
        );

        final String totalBookFieldFontFamily = totalBookField.getFont() != null ? totalBookField.getFont().getFamily() : Font.getDefault().getFamily();
        totalBookField.fontProperty().bind(
                Bindings.createObjectBinding(() -> {
                    double newFontSize = stage.widthProperty().get() / 35.0;
                    return Font.font(totalBookFieldFontFamily, newFontSize);
                }, stage.widthProperty())
        );

        final String totalIssueBookFieldFontFamily = totalIssueBookField.getFont() != null ? totalIssueBookField.getFont().getFamily() : Font.getDefault().getFamily();
        totalIssueBookField.fontProperty().bind(
                Bindings.createObjectBinding(() -> {
                    double newFontSize = stage.widthProperty().get() / 35.0;
                    return Font.font(totalIssueBookFieldFontFamily, newFontSize);
                }, stage.widthProperty())
        );

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            double scale = newVal.doubleValue() / oldVal.doubleValue();

            for (Node node : buttonVBox.getChildren()) {
                if (node instanceof Button) {
                    Button btn = (Button) node;
                    btn.setFont(new Font(btn.getFont().getName(), btn.getFont().getSize() * scale));
                    ImageView graphic = (ImageView) btn.getGraphic();
                    if (graphic != null) {
                        graphic.setFitWidth(graphic.getFitWidth() * scale);
                        graphic.setFitHeight(graphic.getFitHeight() * scale);
                    }
                }
            }

            if (memberSearchField != null) {
                memberSearchField.prefWidthProperty().bind(stage.widthProperty().multiply(0.18));
            }
            if (memberSearchButton != null) {
                ImageView graphic = (ImageView) memberSearchButton.getGraphic();
                if (graphic != null) {
                    graphic.fitWidthProperty().bind(memberSearchButton.widthProperty().multiply(0.25));
                    graphic.fitHeightProperty().bind(memberSearchButton.heightProperty().multiply(0.5));
                }
            }
            if (bookSearchField != null) {
                bookSearchField.prefWidthProperty().bind(stage.widthProperty().multiply(0.25));
            }
            if (searchBookButton != null) {
                ImageView graphic = (ImageView) searchBookButton.getGraphic();
                if (graphic != null) {
                    graphic.fitWidthProperty().bind(searchBookButton.widthProperty().multiply(0.25));
                    graphic.fitHeightProperty().bind(searchBookButton.heightProperty().multiply(0.5));
                }
            }

            if (profileIcon1 != null && profileIcon1.getImage() != null) {
                profileIcon1.fitWidthProperty().bind(leftVBox.widthProperty().multiply(1));
                profileIcon1.fitHeightProperty().bind(leftVBox.widthProperty().multiply(1));
                profileIcon1.setPreserveRatio(true);
            }
        });

        if (memberTabStackPane != null && flowPaneMember != null) {
            flowPaneMember.prefWidthProperty().bind(memberTabStackPane.widthProperty().subtract(18));
            flowPaneMember.prefHeightProperty().bind(memberTabStackPane.heightProperty().subtract(45));
        }

        if (bookTabStackPane != null && flowPaneBook != null) {
            flowPaneBook.prefWidthProperty().bind(bookTabStackPane.widthProperty().subtract(18));
            flowPaneBook.prefHeightProperty().bind(bookTabStackPane.heightProperty().subtract(45));
        }
    }
}