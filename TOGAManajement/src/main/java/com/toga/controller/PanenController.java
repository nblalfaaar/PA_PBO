package com.toga.controller;

import com.toga.dto.PanenDTO;
import com.toga.dto.PenggunaDTO;
import com.toga.repository.impl.PanenRepositoryImpl;
import com.toga.repository.impl.PenggunaRepositoryImpl;
import com.toga.repository.impl.TanamanRepositoryImpl;
import com.toga.service.PanenService;
import com.toga.service.PenggunaService;
import com.toga.service.impl.PanenServiceImpl;
import com.toga.service.impl.PenggunaServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public class PanenController {

    @FXML private ComboBox<String> cmbTanaman;
    @FXML private ComboBox<String> cmbPengguna;
    @FXML private DatePicker       dpTanggalPanen;
    @FXML private TextField        tfHasilPanen;
    @FXML private TextArea         taKeterangan;

    @FXML private TableView<PanenDTO>              tblPanen;
    @FXML private TableColumn<PanenDTO, String>    colTanaman;
    @FXML private TableColumn<PanenDTO, String>    colPengguna;
    @FXML private TableColumn<PanenDTO, String>    colTanggal;
    @FXML private TableColumn<PanenDTO, String>    colHasil;
    @FXML private TableColumn<PanenDTO, String>    colKeterangan;

    private final PanenService panenService =
            new PanenServiceImpl(new PanenRepositoryImpl(), new TanamanRepositoryImpl());
    private final PenggunaService penggunaService =
            new PenggunaServiceImpl(new PenggunaRepositoryImpl());

    private final ObservableList<PanenDTO> data = FXCollections.observableArrayList();

    private final HashMap<String, Integer> tanamanMap  = new HashMap<>();
    private final HashMap<String, Integer> penggunaMap = new HashMap<>();

    private int selectedId = -1;

    @FXML
    public void initialize() {
        dpTanggalPanen.setValue(LocalDate.now());

        colTanaman.setCellValueFactory(new PropertyValueFactory<>("namaTanaman"));
        colPengguna.setCellValueFactory(new PropertyValueFactory<>("namaPengguna"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggalPanenStr"));
        colHasil.setCellValueFactory(new PropertyValueFactory<>("hasilPanen"));
        colKeterangan.setCellValueFactory(new PropertyValueFactory<>("keterangan"));

        tfHasilPanen.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty() && !newVal.matches("[a-zA-Z0-9, ]*"))
                tfHasilPanen.setText(oldVal);
        });

        tblPanen.setOnMouseClicked(e -> {
            PanenDTO row = tblPanen.getSelectionModel().getSelectedItem();
            if (row != null) {
                selectedId = row.getId();
                cmbTanaman.setValue(row.getNamaTanaman());
                cmbPengguna.setValue(row.getNamaPengguna());
                dpTanggalPanen.setValue(row.getTanggalPanen());
                tfHasilPanen.setText(row.getHasilPanen());
                taKeterangan.setText(row.getKeterangan());
            }
        });

        loadCombo();
        loadData();
    }

    @FXML
    public void handleCatat() {
        String    namaTanaman  = cmbTanaman.getValue();
        String    namaPengguna = cmbPengguna.getValue();
        LocalDate tanggal      = dpTanggalPanen.getValue();
        String    hasil        = tfHasilPanen.getText().trim();
        String    keterangan   = taKeterangan.getText().trim();

        if (namaTanaman == null || namaPengguna == null) {
            showAlert("Pilih tanaman dan pengguna!"); return;
        }

        if (namaTanaman.equals("-- Tidak ada tanaman --")) {
            showAlert("Tidak ada tanaman yang siap dipanen!"); return;
        }

        int tanamanId  = tanamanMap.getOrDefault(namaTanaman, -1);
        int penggunaId = penggunaMap.getOrDefault(namaPengguna, -1);
        if (tanamanId == -1 || penggunaId == -1) {
            showAlert("Data tidak valid!"); return;
        }

        try {
            panenService.catatPanen(tanamanId, penggunaId, keterangan, tanggal, hasil);
            loadData();
            loadCombo();
            clearForm();
            showInfo("Panen berhasil dicatat!");
        } catch (IllegalArgumentException ex) {
            showAlert(ex.getMessage());
        }
    }

    @FXML
    public void handleHapus() {
        if (selectedId == -1) { showAlert("Pilih catatan terlebih dahulu!"); return; }
        Alert konfirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Yakin ingin menghapus catatan panen ini?\n" +
                "Status tanaman akan dikembalikan ke kalkulasi otomatis.",
                ButtonType.YES, ButtonType.NO);
        konfirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                panenService.hapusPanen(selectedId);
                loadData();
                loadCombo();
                clearForm();
                showInfo("Catatan panen berhasil dihapus!");
            }
        });
    }

    private void loadCombo() {
        tanamanMap.clear();
        penggunaMap.clear();
        ObservableList<String> tanamanList  = FXCollections.observableArrayList();
        ObservableList<String> penggunaList = FXCollections.observableArrayList();

        try (java.sql.Connection conn = com.toga.config.DBConnection.getConnection()) {
            String sql = "SELECT id, nama FROM tanaman " +
                    "WHERE status != 'SUDAH_DIPANEN' " +  // ← FILTER
                    "ORDER BY nama";
            java.sql.ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                tanamanMap.put(rs.getString("nama"), rs.getInt("id"));
                tanamanList.add(rs.getString("nama"));
            }

            if (tanamanList.isEmpty()) {
                tanamanList.add("-- Tidak ada tanaman --");
            }

        } catch (Exception e) { e.printStackTrace(); }

        List<PenggunaDTO> listPengguna = penggunaService.getAllPengguna();
        for (PenggunaDTO p : listPengguna) {
            penggunaMap.put(p.getNama(), p.getId());
            penggunaList.add(p.getNama());
        }

        cmbTanaman.setItems(tanamanList);
        if (!tanamanList.isEmpty()) cmbTanaman.setValue(tanamanList.getFirst());
        cmbPengguna.setItems(penggunaList);
        if (!penggunaList.isEmpty()) cmbPengguna.setValue(penggunaList.getFirst());
    }

    private void loadData() {
        data.clear();
        List<PanenDTO> list = panenService.getAllPanen();
        data.addAll(list);
        tblPanen.setItems(data);
    }

    private void clearForm() {
        tfHasilPanen.clear();
        taKeterangan.clear();
        dpTanggalPanen.setValue(LocalDate.now());
        selectedId = -1;
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
