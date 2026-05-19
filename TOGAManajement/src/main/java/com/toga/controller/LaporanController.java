package com.toga.controller;

import com.toga.dto.LaporanDTO;
import com.toga.repository.impl.LaporanRepositoryImpl;
import com.toga.service.LaporanService;
import com.toga.service.impl.LaporanServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LaporanController {

    private static final Logger LOGGER = Logger.getLogger(LaporanController.class.getName());

    @FXML private DatePicker dpDari;
    @FXML private DatePicker dpSampai;
    @FXML private Label      lblTotalPanen;
    @FXML private Label      lblTotalPerawatan;
    @FXML private Label      lblPenggunaAktif;

    @FXML private TableView<LaporanDTO.PanenRow>              tblPanen;
    @FXML private TableColumn<LaporanDTO.PanenRow, String>    colTanaman;
    @FXML private TableColumn<LaporanDTO.PanenRow, String>    colPengguna;
    @FXML private TableColumn<LaporanDTO.PanenRow, String>    colTanggal;
    @FXML private TableColumn<LaporanDTO.PanenRow, String>    colHasil;

    @FXML private TableView<LaporanDTO.PerawatanRow>           tblPerawatan;
    @FXML private TableColumn<LaporanDTO.PerawatanRow, String> colTanamanP;
    @FXML private TableColumn<LaporanDTO.PerawatanRow, String> colJenis;
    @FXML private TableColumn<LaporanDTO.PerawatanRow, String> colTanggalP;
    @FXML private TableColumn<LaporanDTO.PerawatanRow, String> colStatusP;

    private final LaporanService laporanService;

    private final ObservableList<LaporanDTO.PanenRow>     dataPanen     = FXCollections.observableArrayList();
    private final ObservableList<LaporanDTO.PerawatanRow> dataPerawatan = FXCollections.observableArrayList();

    public LaporanController() {
        this.laporanService = new LaporanServiceImpl(new LaporanRepositoryImpl());
    }

    @FXML
    public void initialize() {
        dpDari.setValue(LocalDate.now().withDayOfMonth(1));
        dpSampai.setValue(LocalDate.now());

        colTanaman.setCellValueFactory(new PropertyValueFactory<>("namaTanaman"));
        colPengguna.setCellValueFactory(new PropertyValueFactory<>("namaPengguna"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colHasil.setCellValueFactory(new PropertyValueFactory<>("hasil"));

        colTanamanP.setCellValueFactory(new PropertyValueFactory<>("namaTanaman"));
        colJenis.setCellValueFactory(new PropertyValueFactory<>("jenisPerawatan"));
        colTanggalP.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colStatusP.setCellValueFactory(new PropertyValueFactory<>("status"));

        handleTampilkan();
    }

    @FXML
    public void handleTampilkan() {
        LocalDate dari = dpDari.getValue();
        LocalDate sampai = dpSampai.getValue();

        if (dari == null || sampai == null) {
            new Alert(Alert.AlertType.WARNING, "Pilih rentang tanggal!", ButtonType.OK).showAndWait();
            return;
        }

        if (dari.isAfter(sampai)) {
            new Alert(Alert.AlertType.WARNING,
                    "Tanggal awal tidak boleh setelah tanggal akhir!", ButtonType.OK).showAndWait();
            return;
        }

        try {
            loadPanen(dari, sampai);
            loadPerawatan(dari, sampai);
            loadSummary(dari, sampai);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal memuat laporan. Dari: " + dari + ", Sampai: " + sampai, e);
            new Alert(Alert.AlertType.ERROR, "Gagal memuat laporan. Silakan coba lagi.", ButtonType.OK).showAndWait();
        }
    }

    private void loadPanen(LocalDate dari, LocalDate sampai) {
        dataPanen.clear();
        var list = laporanService.getPanenByDateRange(dari, sampai);
        dataPanen.addAll(list);
        tblPanen.setItems(dataPanen);
    }

    private void loadPerawatan(LocalDate dari, LocalDate sampai) {
        dataPerawatan.clear();
        var list = laporanService.getPerawatanByDateRange(dari, sampai);
        dataPerawatan.addAll(list);
        tblPerawatan.setItems(dataPerawatan);
    }

    private void loadSummary(LocalDate dari, LocalDate sampai) {
        var summary = laporanService.getSummaryByDateRange(dari, sampai);
        lblTotalPanen.setText(String.valueOf(summary.getTotalPanen()));
        lblTotalPerawatan.setText(String.valueOf(summary.getTotalPerawatan()));
        lblPenggunaAktif.setText(String.valueOf(summary.getPenggunaAktif()));
    }
}