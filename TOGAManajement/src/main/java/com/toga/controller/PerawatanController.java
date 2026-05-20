package com.toga.controller;

import com.toga.dto.PerawatanDTO;
import com.toga.dto.PenggunaDTO;
import com.toga.repository.impl.PerawatanRepositoryImpl;
import com.toga.repository.impl.PenggunaRepositoryImpl;
import com.toga.repository.impl.TanamanRepositoryImpl;
import com.toga.service.PerawatanService;
import com.toga.service.PenggunaService;
import com.toga.service.TanamanService;
import com.toga.service.impl.PerawatanServiceImpl;
import com.toga.service.impl.PenggunaServiceImpl;
import com.toga.service.impl.TanamanServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PerawatanController {

    private static final Logger LOGGER = Logger.getLogger(PerawatanController.class.getName());

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

    private final PerawatanService perawatanService;
    private final PenggunaService penggunaService;
    private final TanamanService tanamanService;

    private final ObservableList<PerawatanDTO> data = FXCollections.observableArrayList();

    private final Map<String, Integer> tanamanMap = new HashMap<>();
    private final Map<String, Integer> penggunaMap = new HashMap<>();

    private int selectedId = -1;

    public PerawatanController() {
        this.perawatanService = new PerawatanServiceImpl(new PerawatanRepositoryImpl());
        this.penggunaService = new PenggunaServiceImpl(new PenggunaRepositoryImpl());
        this.tanamanService = new TanamanServiceImpl(new TanamanRepositoryImpl());
    }

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
        String namaTanaman = cmbTanaman.getValue();
        String jenis = cmbJenisPerawatan.getValue();
        LocalDate tanggal = dpTanggal.getValue();

        if (namaTanaman == null || jenis == null || tanggal == null) {
            showAlert("Semua field harus diisi!");
            return;
        }

        Integer tanamanId = tanamanMap.get(namaTanaman);
        if (tanamanId == null) {
            showAlert("Tanaman tidak ditemukan!");
            return;
        }

        try {
            perawatanService.tambahJadwal(tanamanId, jenis, tanggal);
            loadData();
            showInfo("Jadwal berhasil ditambahkan!");
        } catch (IllegalArgumentException ex) {
            showAlert(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Gagal tambah jadwal", ex);
            showAlert("Terjadi kesalahan sistem. Silakan coba lagi.");
        }
    }

    @FXML
    public void handleTandaiSelesai() {
        if (selectedId == -1) {
            showAlert("Pilih jadwal terlebih dahulu!");
            return;
        }

        String namaPengguna = cmbPengguna.getValue();
        if (namaPengguna == null) {
            showAlert("Pilih pengguna yang melakukan perawatan!");
            return;
        }

        Integer penggunaId = penggunaMap.get(namaPengguna);
        if (penggunaId == null) {
            showAlert("Pengguna tidak ditemukan!");
            return;
        }

        try {
            perawatanService.tandaiSelesai(selectedId, penggunaId, namaPengguna);
            loadData();
            showInfo("Jadwal ditandai selesai dan dicatat oleh " + namaPengguna + "!");
        } catch (IllegalArgumentException ex) {
            showAlert(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Gagal tandai selesai jadwal id: " + selectedId, ex);
            showAlert("Terjadi kesalahan sistem. Silakan coba lagi.");
        }
    }

    @FXML
    public void handleHapus() {
        if (selectedId == -1) {
            showAlert("Pilih jadwal terlebih dahulu!");
            return;
        }

        Alert konfirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Yakin ingin menghapus jadwal ini?", ButtonType.YES, ButtonType.NO);

        konfirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    perawatanService.hapusJadwal(selectedId);
                    loadData();
                    selectedId = -1;
                    showInfo("Jadwal berhasil dihapus!");
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Gagal hapus jadwal id: " + selectedId, ex);
                    showAlert("Gagal menghapus: " + ex.getMessage());
                }
            }
        });
    }

    private void loadCombo() {
        tanamanMap.clear();
        penggunaMap.clear();

        ObservableList<String> tanamanList = FXCollections.observableArrayList();
        ObservableList<String> penggunaList = FXCollections.observableArrayList();

        try {
            var tanamanDTOList = tanamanService.getAllTanamanForPerawatan();
            if (tanamanDTOList.isEmpty()) {
                tanamanList.add("-- Tidak ada tanaman aktif --");
            } else {
                for (var dto : tanamanDTOList) {
                    tanamanMap.put(dto.getNama(), dto.getId());
                    tanamanList.add(dto.getNama());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal load tanaman combo", e);
            tanamanList.add("-- Gagal memuat data --");
        }

        try {
            List<PenggunaDTO> listPengguna = penggunaService.getAllPengguna();
            if (listPengguna.isEmpty()) {
                penggunaList.add("-- Tidak ada pengguna --");
            } else {
                for (PenggunaDTO p : listPengguna) {
                    penggunaMap.put(p.getNama(), p.getId());
                    penggunaList.add(p.getNama());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal load pengguna combo", e);
            penggunaList.add("-- Gagal memuat data --");
        }

        cmbTanaman.setItems(tanamanList);
        if (!tanamanList.isEmpty() && !tanamanList.getFirst().equals("-- Tidak ada tanaman aktif --")) {
            cmbTanaman.setValue(tanamanList.getFirst());
        }

        cmbPengguna.setItems(penggunaList);
        if (!penggunaList.isEmpty() && !penggunaList.getFirst().equals("-- Tidak ada pengguna --")) {
            cmbPengguna.setValue(penggunaList.getFirst());
        }
    }

    private void loadData() {
        data.clear();
        try {
            List<PerawatanDTO> list = perawatanService.getAllJadwal();
            data.addAll(list);
            tblJadwal.setItems(data);

            int belum = perawatanService.countBelumHariIni();
            lblBelumHariIni.setText(belum + " jadwal belum dilakukan hari ini");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal load data perawatan", e);
            showAlert("Gagal memuat data perawatan");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.setTitle("Peringatan");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}