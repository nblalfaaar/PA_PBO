package com.toga.controller;

import com.toga.dto.DashboardDTO;
import com.toga.repository.impl.DashboardRepositoryImpl;
import com.toga.repository.impl.TanamanRepositoryImpl;
import com.toga.service.DashboardService;
import com.toga.service.TanamanService;
import com.toga.service.impl.DashboardServiceImpl;
import com.toga.service.impl.TanamanServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DashboardController {
    private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());

    @FXML private Label lblTotalTanaman;
    @FXML private Label lblSiapPanen;
    @FXML private Label lblTotalPengguna;
    @FXML private Label lblJadwalHariIni;
    @FXML private Label lblJmlRempah;
    @FXML private Label lblJmlDaun;
    @FXML private Label lblJmlBuah;

    @FXML private TableView<DashboardDTO.MendekatiPanenRow> tblMendekatiPanen;
    @FXML private TableColumn<DashboardDTO.MendekatiPanenRow, String> colNama;
    @FXML private TableColumn<DashboardDTO.MendekatiPanenRow, String> colSisa;

    private final DashboardService dashboardService;
    private final TanamanService tanamanService;

    public DashboardController() {
        this.dashboardService = new DashboardServiceImpl(new DashboardRepositoryImpl());
        this.tanamanService = new TanamanServiceImpl(new TanamanRepositoryImpl());
    }

    @FXML
    public void initialize() {
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colSisa.setCellValueFactory(new PropertyValueFactory<>("sisa"));
        loadDashboard();
    }

    private void loadDashboard() {
        try {
            tanamanService.updateAllStatusOtomatis();

            DashboardDTO data = dashboardService.getDashboardData();

            lblTotalTanaman.setText(String.valueOf(data.getTotalTanaman()));
            lblTotalPengguna.setText(String.valueOf(data.getTotalPengguna()));
            lblJadwalHariIni.setText(String.valueOf(data.getJadwalHariIni()));
            lblJmlRempah.setText(data.getJumlahRempah() + " tanaman");
            lblJmlDaun.setText(data.getJumlahDaun() + " tanaman");
            lblJmlBuah.setText(data.getJumlahBuah() + " tanaman");
            lblSiapPanen.setText(String.valueOf(data.getSiapPanen()));

            tblMendekatiPanen.setItems(data.getMendekatiPanen());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal memuat data dashboard", e);
            showError("Gagal memuat data dashboard. Silakan coba lagi.");
        }
    }

    private void showError(String msg) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR, msg,
                javafx.scene.control.ButtonType.OK
        );
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}