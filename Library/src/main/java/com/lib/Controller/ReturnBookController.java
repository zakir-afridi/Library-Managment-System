package com.lib.Controller;

import com.lib.Function.ConnectionTest;
import com.lib.Function.DatabaseFunction;
import com.lib.Function.Function;
import com.lib.Function.RetriveLibraryDetails;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ReturnBookController {

    @FXML private TextField memberIdField;
    @FXML private Button searchMemberButton;
    @FXML private TextField memberIdField1;

    @FXML private TextField memberNameField;
    @FXML private TextField bookNameField;
    @FXML private Button returnButton;
    @FXML private TextField bookSearchField;
    @FXML private Button searchBookButton;
    @FXML private Button BookRefreshButton;
    @FXML private FlowPane flowPaneBook;

    private final DatabaseFunction dbFunction = new DatabaseFunction();
    private final RetriveLibraryDetails retriever = new RetriveLibraryDetails();
    private final ConnectionTest connectionTest = new ConnectionTest();
    private final DeshboardController dashboard = new DeshboardController();
    private final Function functionUtil = new Function();
    private final DatabaseFunction retriveTotal = new DatabaseFunction();


    private final String styleForTab = "-fx-background-color: rgba(0,0,0,0.8); " +
            "-fx-background-radius: 15; " +
            "-fx-border-width: 5; " +
            "-fx-border-color: linear-gradient(to right, #ffa8B6, #51e2f5); " +
            "-fx-border-radius: 15; " +
            "-fx-padding: 15;";

    private final String buttonStyle = "-fx-background-color: rgb(42,60,87); " +
            "-fx-background-radius: 15; " +
            "-fx-border-width: 2; " +
            "-fx-border-color: linear-gradient(to right, #ffa8B6, #51e2f5); " +
            "-fx-border-radius: 15; " +
            "-fx-padding: 15;";


    private final String defaultImageQuery = "SELECT profile_pic FROM books INNER JOIN issue_books ON books.book_id = issue_books.book_id;";
    private final String defaultIdQuery = "SELECT book_id FROM issue_books;";
    private final String defaultNameQuery = "SELECT book_name FROM books INNER JOIN issue_books ON books.book_id = issue_books.book_id;";

    @FXML
    private void initialize() {
        loadBookImages(defaultImageQuery, defaultIdQuery, defaultNameQuery);
        setupButtonActions();
        searchBookButton.setStyle(buttonStyle);
        BookRefreshButton.setStyle(buttonStyle);
        returnButton.setStyle(buttonStyle);
    }

    private void setupButtonActions() {
        searchBookButton.setOnAction(event -> handleSearchBook());
        BookRefreshButton.setOnAction(event -> handleRefreshBooks());
        searchMemberButton.setOnAction(event -> handleSearchMember(memberIdField.getText().trim() ,memberIdField1.getText().trim()) );
        returnButton.setOnAction(event -> handleReturnBook());
    }

    private void handleSearchBook() {
        String searchText = bookSearchField.getText().trim();
        flowPaneBook.getChildren().clear();

        if (searchText.isEmpty()) {
            loadBookImages(defaultImageQuery, defaultIdQuery, defaultNameQuery);
            return;
        }


        String imageQuery = "SELECT profile_pic FROM books " +
                "INNER JOIN issue_books ON books.book_id = issue_books.book_id " +
                "WHERE issue_books.book_id = '" + searchText + "' " +
                "OR LOWER(books.book_name) LIKE LOWER('%" + searchText + "%');";

        String idQuery = "SELECT books.book_id FROM books " +
                "INNER JOIN issue_books ON books.book_id = issue_books.book_id " +
                "WHERE books.book_id = '" + searchText + "' " +
                "OR LOWER(books.book_name) LIKE LOWER('%" + searchText + "%');";

        String nameQuery = "SELECT book_name FROM books " +
                "INNER JOIN issue_books ON books.book_id = issue_books.book_id " +
                "WHERE books.book_id = '" + searchText + "' " +
                "OR LOWER(books.book_name) LIKE LOWER('%" + searchText + "%');";




        loadBookImages(imageQuery, idQuery, nameQuery);
    }


    private void handleSearchMember(String memberId,String bookId) {



            if (memberId.isEmpty() || bookId.isEmpty()) {
                functionUtil.alertW("Warning!", "Please enter all IDs.");
                return;
            }

            String memberNameQuery = "SELECT name FROM members " +
                    "INNER JOIN issue_books ON members.std_id = issue_books.std_id " +
                    "WHERE issue_books.std_id = '" + memberId + "' AND issue_books.book_id = '" + bookId + "'";

            String bookNameQuery = "SELECT book_name FROM books " +
                    "INNER JOIN issue_books ON books.book_id = issue_books.book_id " +
                    "WHERE issue_books.book_id = '" + bookId + "'";

            ArrayList<String> memberNames = retriveTotal.retrieveNames(memberNameQuery, "name");
            ArrayList<String> bookNames = retriveTotal.retrieveNames(bookNameQuery, "book_name");

            if (!memberNames.isEmpty() && !bookNames.isEmpty()) {
                memberNameField.setText(memberNames.get(0));
                bookNameField.setText(bookNames.get(0));
            } else {
                functionUtil.alertW("Warning!", "No matching record found for this member and book.");
                memberNameField.clear();
                bookNameField.clear();
                memberIdField.requestFocus();
            }


    }

    private void handleReturnBook() {
        String memberid = memberIdField.getText();
        String bookid = memberIdField1.getText();

        if (memberid.isEmpty() || bookid.isEmpty()) {
            functionUtil.alertW("Warning!", "Please enter member ID and Book ID  to return.");
            return;
        }
        Connection con = connectionTest.getConnection();
        String q = "DELETE FROM issue_books WHERE std_id = ? and book_id=?";


        try (PreparedStatement pst = con.prepareStatement(q)) {
            pst.setInt(1, Integer.parseInt(memberIdField.getText().trim()));
            pst.setInt(2,Integer.parseInt(memberIdField1.getText().trim()));

            boolean confirmed = functionUtil.alertConfirm("Are you sure?", "Do you  want to Return this Book?");
            if (confirmed) {
                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    functionUtil.alertW("Returned","Book Successfully Returned");
                    bookUpadate(Integer.parseInt(bookid));
                    saveReturnDetails( Integer.parseInt(memberIdField.getText().trim()),
                            Integer.parseInt(memberIdField1.getText().trim()));
                } else {
                    functionUtil.alertW("Error","Book Can Not Returned");
                }
            } else {
                functionUtil.alertW("Error","Book Can Not Returned");
            }





        } catch (SQLException e) {
            functionUtil.alertW("Warning!",e.getMessage());
        }

    }

    private void handleRefreshBooks() {
        bookSearchField.clear();
        flowPaneBook.getChildren().clear();
        loadBookImages(defaultImageQuery, defaultIdQuery, defaultNameQuery);
    }

    public void loadBookImages(String imageQuery, String idQuery, String nameQuery) {
        flowPaneBook.getChildren().clear();

        ArrayList<Integer> bookIds = retriveTotal.retrieveId(idQuery, "book_id");
        ArrayList<String> bookNames = retriveTotal.retrieveNames(nameQuery, "book_name");

        String memberIdQuery = "SELECT members.std_id FROM members " +
                "INNER JOIN issue_books ON members.std_id = issue_books.std_id ";


        String memberNameQuery = "SELECT name FROM members " +
                "INNER JOIN issue_books ON members.std_id = issue_books.std_id " ;

        ArrayList<Integer> memberIds = retriveTotal.retrieveId(memberIdQuery, "std_id");
        ArrayList<String> memberNames = retriveTotal.retrieveNames(memberNameQuery, "name");
        ImageView[] bookImages = dbFunction.getImagesFromDatabase(imageQuery, bookIds.size());

        for (int i = 0; i < bookIds.size(); i++) {
            if (i < bookImages.length && i < bookNames.size() && bookImages[i] != null) {
                ImageView imgView = bookImages[i];
                int bookId = bookIds.get(i);

                int stdId = memberIds.get(i);

                imgView.setUserData(bookId);
                imgView.setCursor(Cursor.HAND);

                imgView.setUserData(bookId);

                Label nameLabel = new Label("BookName: " + bookNames.get(i) + "\nMemberID: " + memberIds.get(i)
                        + "\nMemberName: " + memberNames.get(i));
                nameLabel.setWrapText(true);
                nameLabel.setMaxWidth(120);
                nameLabel.setAlignment(Pos.CENTER);
                nameLabel.getStyleClass().add("member-name");

                VBox container = new VBox(5, imgView, nameLabel);
                container.setAlignment(Pos.CENTER);
                container.setStyle(styleForTab);


                container.setCursor(Cursor.HAND);

                container.setOnMouseClicked(e -> {
                    memberIdField.setText(String.valueOf(stdId));
                    memberIdField1.setText(String.valueOf(bookId));

                    setDataFromImage(bookId);
                });

                flowPaneBook.getChildren().add(container);


            }
        }
    }
    private  void setDataFromImage(int id){


        String memberNameQuery = "SELECT name FROM members " +
                "INNER JOIN issue_books ON members.std_id = issue_books.std_id " +
                "WHERE issue_books.book_id = '" + id + "';";
        String bookNameQuery = "SELECT book_name FROM books " +
                "INNER JOIN issue_books ON books.book_id = issue_books.book_id " +
                "WHERE issue_books.book_id = '" + id + "';";

        ArrayList<String> memberNames = retriveTotal.retrieveNames(memberNameQuery, "name");
        ArrayList<String> bookNames = retriveTotal.retrieveNames(bookNameQuery, "book_name");

        if (!memberNames.isEmpty() && !bookNames.isEmpty()) {
            memberNameField.clear();
            bookNameField.clear();
            memberNameField.setText(memberNames.get(0));
            bookNameField.setText(bookNames.get(0));
        } else {
            functionUtil.alertW("Warning!", "Member doesn't have any books issued.");
            memberNameField.clear();
            bookNameField.clear();
            memberIdField.requestFocus();
        }
    }

    private void bookUpadate(int id){
        Connection con=connectionTest.getConnection();
        try {
            String q="update books set quantity=quantity+1 where book_id=?;";
            PreparedStatement pst=con.prepareStatement(q);
            pst.setInt(1,id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private  void saveReturnDetails(int memberId ,int BookId){
        try {
            Connection con = connectionTest.getConnection();
            String q = "INSERT INTO return_book(std_id, book_id) VALUES(?, ?)";
            PreparedStatement pst = con.prepareStatement(q);
            pst.setInt(1, memberId);
            pst.setInt(2, BookId);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}