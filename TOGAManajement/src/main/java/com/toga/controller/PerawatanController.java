package com.toga.controller;

import com.toga.dto.PerawatanDTO;
import com.toga.dto.PenggunaDTO;
import com.toga.repository.impl.PerawatanRepositoryImpl;
import com.toga.repository.impl.PenggunaRepositoryImpl;
import com.toga.service.PerawatanService;
import com.toga.service.PenggunaService;
import com.toga.service.impl.PerawatanServiceImpl;
import com.toga.service.impl.PenggunaServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public class PerawatanController {

    @FXML private ComboBox<String> cmbTanaman;
    @FXML private ComboBox<String> cmbJenisPerawatan;
    @FXML private DatePicker       dpTanggal;
    @FXML private ComboBox<String> cmbPengguna;
    @FXML private Label            lblBelumHariIni;

    @FXML private TableView<PerawatanDTO>              tblJadwal;
    @FXML private TableColumn<PerawatanDTO, String>    colTanaman;
    @FXML private TableColumn<PerawatanDTO, String>    colJenis;
    @FXML private TableColumn<PerawatanDTO, String>    colTanggal;
    @FXML private TableColumn<PerawatanDTO, String>    colStatus;
    @FXML private TableColumn<PerawatanDTO, String>    colPetugas;

    private final PerawatanService perawatanService =
            new PerawatanServiceImpl(new PerawatanRepositoryImpl());
    private final PenggunaService penggunaService =
            new PenggunaServiceImpl(new PenggunaRepositoryImpl());

    private final ObservableList<PerawatanDTO> data = FXCollections.observableArrayList();

    // Map nama → id untuk tanaman dan pengguna
    private final HashMap<String, Integer> tanamanMap  = new HashMap<>();
    private final HashMap<String, Integer> penggunaMap = new HashMap<>();

    private int selectedId = -1;

    @FXML
    public void initialize() {
        cmbJenisPerawatan.setItems(FXCollections.observableArrayList(
                "Penyiraman", "Pemupukan", "Penyiangan", "Pemangkasan"));
        cmbJenisPerawatan.setValue("Penyiraman");
        dpTanggal.setValue(LocalDate.now());

        colTanaman.setCellValueFactory(new PropertyValueFactory<>("namaTanaman"));
        colJenis.setCellValueFactory(new PropertyValueFactory<>("jenisPerawatan"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggalStr"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPetugas.setCellValueFactory(new PropertyValueFactory<>("namaPetugas"));

        tblJadwal.setOnMouseClicked(e -> {
            PerawatanDTO row = tblJadwal.getSelectionModel().getSelectedItem();
            if (row != null) selectedId = row.getId();
        });

        loadCombo();
        loadData();
    }

    @FXML
    public void handleTambah() {
        String    namaTanaman = cmbTanaman.getValue();
        String    jenis       = cmbJenisPerawatan.getValue();
        LocalDate tanggal     = dpTanggal.getValue();

        if (namaTanaman == null || jenis == null || tanggal == null) {
            showAlert("Semua field harus diisi!"); return;
        }

        int tanamanId = tanamanMap.getOrDefault(namaTanaman, -1);
        if (tanamanId == -1) { showAlert("Tanaman tidak ditemukan!"); return; }

        try {
            perawatanService.tambahJadwal(tanamanId, jenis, tanggal);
            loadData();
            showInfo("Jadwal berhasil ditambahkan!");
        } catch (IllegalArgumentException ex) {
            showAlert(ex.getMessage());
        }
    }

    @FXML
    public void handleTandaiSelesai() {
        if (selectedId == -1) { showAlert("Pilih jadwal terlebih dahulu!"); return; }

        String namaPengguna = cmbPengguna.getValue();
        if (namaPengguna == null) {
            showAlert("Pilih pengguna yang melakukan perawatan!"); return;
        }

        int penggunaId = penggunaMap.getOrDefault(namaPengguna, -1);
        if (penggunaId == -1) { showAlert("Pengguna tidak ditemukan!"); return; }

        try {
            perawatanService.tandaiSelesai(selectedId, penggunaId, namaPengguna);
            loadData();
            showInfo("Jadwal ditandai selesai dan dicatat oleh " + namaPengguna + "!");
        } catch (IllegalArgumentException ex) {
            showAlert(ex.getMessage());
        }
    }

    @FXML
    public void handleHapus() {
        if (selectedId == -1) { showAlert("Pilih jadwal terlebih dahulu!"); return; }
        Alert konfirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Yakin ingin menghapus jadwal ini?", ButtonType.YES, ButtonType.NO);
        konfirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                perawatanService.hapusJadwal(selectedId);
                loadData();
                selectedId = -1;
                showInfo("Jadwal berhasil dihapus!");
            }
        });
    }

    private void loadCombo() {
        tanamanMap.clear();
        penggunaMap.clear();
        ObservableList<String> tanamanList  = FXCollections.observableArrayList();
        ObservableList<String> penggunaList = FXCollections.observableArrayList();

        try (java.sql.Connection conn = com.toga.config.DBConnection.getConnection()) {
            java.sql.ResultSet rs1 = conn.createStatement()
                    .executeQuery("SELECT id, nama FROM tanaman ORDER BY nama");
            while (rs1.next()) {
                tanamanMap.put(rs1.getString("nama"), rs1.getInt("id"));
                tanamanList.add(rs1.getString("nama"));
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
        List<PerawatanDTO> list = perawatanService.getAllJadwal();
        data.addAll(list);
        tblJadwal.setItems(data);
        int belum = perawatanService.countBelumHariIni();
        lblBelumHariIni.setText(belum + " jadwal belum dilakukan hari ini");
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
