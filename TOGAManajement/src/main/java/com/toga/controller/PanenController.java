package com.toga.controller;

import com.toga.dto.PanenDTO;
import com.toga.dto.PenggunaDTO;
import com.toga.repository.impl.PanenRepositoryImpl;
import com.toga.repository.impl.PenggunaRepositoryImpl;
import com.toga.repository.impl.TanamanRepositoryImpl;
import com.toga.service.PanenService;
import com.toga.service.PenggunaService;
import com.toga.service.TanamanService;
import com.toga.service.impl.PanenServiceImpl;
import com.toga.service.impl.PenggunaServiceImpl;
import com.toga.service.impl.TanamanServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PanenController {

    private static final Logger LOGGER = Logger.getLogger(PanenController.class.getName());

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

    private final PanenService panenService;
    private final PenggunaService penggunaService;
    private final TanamanService tanamanService;

    private final ObservableList<PanenDTO> data = FXCollections.observableArrayList();

    private final Map<String, Integer> tanamanMap = new HashMap<>();
    private final Map<String, Integer> penggunaMap = new HashMap<>();

    private int selectedId = -1;

    public PanenController() {
        this.panenService = new PanenServiceImpl(new PanenRepositoryImpl(), new TanamanRepositoryImpl());
        this.penggunaService = new PenggunaServiceImpl(new PenggunaRepositoryImpl());
        this.tanamanService = new TanamanServiceImpl(new TanamanRepositoryImpl());
    }

    @FXML
    public void initialize() {
        dpTanggalPanen.setValue(LocalDate.now());

        colTanaman.setCellValueFactory(new PropertyValueFactory<>("namaTanaman"));
        colPengguna.setCellValueFactory(new PropertyValueFactory<>("namaPengguna"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggalPanenStr"));
        colHasil.setCellValueFactory(new PropertyValueFactory<>("hasilPanenStr"));
        colKeterangan.setCellValueFactory(new PropertyValueFactory<>("keterangan"));

        tfHasilPanen.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                if (!newVal.matches("\\d*")) {
                    tfHasilPanen.setText(oldVal);
                }
            }
        });

        tblPanen.setOnMouseClicked(e -> {
            PanenDTO row = tblPanen.getSelectionModel().getSelectedItem();
            if (row != null) {
                selectedId = row.getId();
                cmbTanaman.setValue(row.getNamaTanaman());
                cmbPengguna.setValue(row.getNamaPengguna());
                dpTanggalPanen.setValue(row.getTanggalPanen());
                tfHasilPanen.setText(String.valueOf(row.getHasilPanen()));
                taKeterangan.setText(row.getKeterangan());
            }
        });

        loadCombo();
        loadData();
    }

    @FXML
    public void handleCatat() {
        String namaTanaman = cmbTanaman.getValue();
        String namaPengguna = cmbPengguna.getValue();
        LocalDate tanggal = dpTanggalPanen.getValue();
        String hasilStr = tfHasilPanen.getText().trim();
        String keterangan = taKeterangan.getText().trim();

        if (namaTanaman == null || namaPengguna == null) {
            showAlert("Pilih tanaman dan pengguna!");
            return;
        }

        if (namaTanaman.equals("-- Tidak ada tanaman --")) {
            showAlert("Tidak ada tanaman yang siap dipanen!");
            return;
        }

        int hasil;
        try {
            hasil = Integer.parseInt(hasilStr);
            if (hasil <= 0) {
                showAlert("Hasil panen harus berupa angka positif!");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Hasil panen harus berupa angka!");
            return;
        }

        Integer tanamanId = tanamanMap.get(namaTanaman);
        Integer penggunaId = penggunaMap.get(namaPengguna);

        if (tanamanId == null || penggunaId == null) {
            showAlert("Data tidak valid!");
            return;
        }

        try {
            panenService.catatPanen(tanamanId, penggunaId, keterangan, tanggal, hasil);
            loadData();
            loadCombo();
            clearForm();
            showInfo("Panen berhasil dicatat!");
        } catch (IllegalArgumentException ex) {
            showAlert(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Gagal mencatat panen", ex);
            showAlert("Terjadi kesalahan sistem. Silakan coba lagi.");
        }
    }

    @FXML
    public void handleHapus() {
        if (selectedId == -1) {
            showAlert("Pilih catatan terlebih dahulu!");
            return;
        }

        Alert konfirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Yakin ingin menghapus catatan panen ini?\n" +
                        "Status tanaman akan dikembalikan ke kalkulasi otomatis.",
                ButtonType.YES, ButtonType.NO);

        konfirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    panenService.hapusPanen(selectedId);
                    loadData();
                    loadCombo();
                    clearForm();
                    showInfo("Catatan panen berhasil dihapus!");
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Gagal menghapus panen id: " + selectedId, ex);
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
            var tanamanDTOList = tanamanService.getAllTanamanForCombo();
            if (tanamanDTOList.isEmpty()) {
                tanamanList.add("-- Tidak ada tanaman --");
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
            for (PenggunaDTO p : listPengguna) {
                penggunaMap.put(p.getNama(), p.getId());
                penggunaList.add(p.getNama());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal load pengguna combo", e);
            penggunaList.add("-- Gagal memuat data --");
        }

        cmbTanaman.setItems(tanamanList);
        if (!tanamanList.isEmpty() && !tanamanList.getFirst().equals("-- Tidak ada tanaman --")) {
            cmbTanaman.setValue(tanamanList.getFirst());
        }

        cmbPengguna.setItems(penggunaList);
        if (!penggunaList.isEmpty()) {
            cmbPengguna.setValue(penggunaList.getFirst());
        }
    }

    private void loadData() {
        data.clear();
        try {
            List<PanenDTO> list = panenService.getAllPanen();
            data.addAll(list);
            tblPanen.setItems(data);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal load data panen", e);
            showAlert("Gagal memuat data panen");
        }
    }

    private void clearForm() {
        tfHasilPanen.clear();
        taKeterangan.clear();
        dpTanggalPanen.setValue(LocalDate.now());
        selectedId = -1;
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