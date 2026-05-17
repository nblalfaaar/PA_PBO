package com.toga.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        showDashboard();
    }

    @FXML
    public void showDashboard() {
        loadPage("/com/toga/view/DashboardView.fxml");
    }

    @FXML
    public void showTanaman() {
        loadPage("/com/toga/view/TanamanView.fxml");
    }

    @FXML
    public void showPengguna() {
        loadPage("/com/toga/view/PenggunaView.fxml");
    }

    @FXML
    public void showPerawatan() {
        loadPage("/com/toga/view/PerawatanView.fxml");
    }

    @FXML
    public void showPanen() {
        loadPage("/com/toga/view/PanenView.fxml");
    }

    @FXML
    public void showLaporan() {
        loadPage("/com/toga/view/LaporanView.fxml");
    }

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Gagal memuat: " + fxmlPath);
        }
    }
}