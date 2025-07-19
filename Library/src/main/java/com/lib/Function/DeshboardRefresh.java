package com.lib.Function;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class DeshboardRefresh {
    public  void refresh(TextArea text) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lib/library/Deshboard - Copy.fxml"));

            String css = getClass().getResource("/com/lib/library/CSS/Dashboard.css").toExternalForm();


            Parent root = loader.load();
            root.getStylesheets().add(css);

            Stage newsStage = new Stage();
            newsStage.setScene(new Scene(root));
            newsStage.show();

            Stage currentStage = (Stage) text.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }


    public  void refresh(TextField field) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lib/library/Deshboard - Copy.fxml"));

            String css = getClass().getResource("/com/lib/library/CSS/Dashboard.css").toExternalForm();


            Parent root = loader.load();
            root.getStylesheets().add(css);

            Stage newsStage = new Stage();
            newsStage.setScene(new Scene(root));
            newsStage.show();

            Stage currentStage = (Stage) field.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

        public   void onlyRefresh(){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lib/library/Deshboard - Copy.fxml"));
                String css = getClass().getResource("/com/lib/library/CSS/Dashboard.css").toExternalForm();
                Parent root = loader.load();
                root.getStylesheets().add(css);
                Stage newsStage = new Stage();
                newsStage.setScene(new Scene(root));
                newsStage.show();
            } catch (IOException e) {
                e.printStackTrace();

            }
    }
}


