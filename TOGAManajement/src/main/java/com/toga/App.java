package com.toga;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.logging.Logger;

public class App extends Application {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/toga/view/MainView.fxml"));
        Scene scene = new Scene(loader.load(), 960, 620);

        URL cssUrl = getClass().getResource("/com/toga/css/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
            LOGGER.info("CSS berhasil dimuat");
        } else {
            LOGGER.warning("File CSS tidak ditemukan di /com/toga/css/style.css");
        }

        stage.setTitle("Sistem Manajemen TOGA");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }
}