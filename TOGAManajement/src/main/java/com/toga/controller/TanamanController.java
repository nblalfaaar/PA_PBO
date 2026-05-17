package com.toga.controller;

import com.toga.dto.TanamanDTO;
import com.toga.repository.impl.TanamanRepositoryImpl;
import com.toga.service.TanamanService;
import com.toga.service.impl.TanamanServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class TanamanController {

    @FXML private ComboBox<String> cmbJenis;
    @FXML private TextField        tfNama;
    @FXML private TextField        tfNamaLatin;
    @FXML private TextField        tfProperti;
    @FXML private TextField        tfEstimasiHari;
    @FXML private TextArea         taManfaat;
    @FXML private DatePicker       dpTanggal;
    @FXML private Label            lblProperti;
    @FXML private Label            lblStatusInfo;

    @FXML private TableView<TanamanDTO>              tblTanaman;
    @FXML private TableColumn<TanamanDTO, String>    colNama;
    @FXML private TableColumn<TanamanDTO, String>    colJenis;
    @FXML private TableColumn<TanamanDTO, String>    colNamaLatin;
    @FXML private TableColumn<TanamanDTO, Integer>   colEstimasi;
    @FXML private TableColumn<TanamanDTO, String>    colStatus;

    private final TanamanService tanamanService =
            new TanamanServiceImpl(new TanamanRepositoryImpl());

    private final ObservableList<TanamanDTO> data = FXCollections.observableArrayList();
    private int selectedId = -1;

    @FXML
    public void initialize() {
        cmbJenis.setItems(FXCollections.observableArrayList(
                "Tanaman Rempah", "Tanaman Daun", "Tanaman Buah"));
        cmbJenis.setValue("Tanaman Rempah");
        dpTanggal.setValue(LocalDate.now());

        cmbJenis.setOnAction(e -> updateLabelProperti());
        dpTanggal.valueProperty().addListener((obs, o, n) -> updateStatusPreview());

        pasangFilterHurufSpasi(tfNama);
        pasangFilterHurufSpasi(tfNamaLatin);
        pasangFilterHurufSpasi(tfProperti);

        // Filter angka saja untuk estimasi hari
        tfEstimasiHari.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.matches("\\d*"))
                tfEstimasiHari.setText(oldVal);
        });

        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colJenis.setCellValueFactory(new PropertyValueFactory<>("jenis"));
        colNamaLatin.setCellValueFactory(new PropertyValueFactory<>("namaLatin"));
        colEstimasi.setCellValueFactory(new PropertyValueFactory<>("estimasiHari"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        tblTanaman.setOnMouseClicked(e -> {
            TanamanDTO row = tblTanaman.getSelectionModel().getSelectedItem();
            if (row != null) {
                selectedId = row.getId();
                tfNama.setText(row.getNama());
                tfNamaLatin.setText(row.getNamaLatin());
                taManfaat.setText(row.getManfaat());
                tfProperti.setText(row.getPropertiTambahan());
                cmbJenis.setValue(row.getJenis());
                dpTanggal.setValue(row.getTanggalTanam());
                tfEstimasiHari.setText(String.valueOf(row.getEstimasiHari()));
                lblStatusInfo.setText("Status saat ini: " + row.getStatus());
            }
        });

        updateStatusPreview();
        loadData();
    }

    @FXML
    public void handleTambah() {
        try {
            TanamanDTO dto = buildDTOFromForm(-1);
            tanamanService.tambahTanaman(dto);
            loadData();
            clearForm();
            showInfo("Tanaman berhasil ditambahkan! Status: " + dto.getStatus());
        } catch (IllegalArgumentException ex) {
            showAlert(ex.getMessage());
        }
    }

    @FXML
    public void handleUbah() {
        if (selectedId == -1) { showAlert("Pilih tanaman terlebih dahulu!"); return; }
        try {
            TanamanDTO dto = buildDTOFromForm(selectedId);
            tanamanService.ubahTanaman(dto);
            loadData();
            clearForm();
            showInfo("Tanaman berhasil diubah! Status: " + dto.getStatus());
        } catch (IllegalArgumentException ex) {
            showAlert(ex.getMessage());
        }
    }

    @FXML
    public void handleHapus() {
        if (selectedId == -1) { showAlert("Pilih tanaman terlebih dahulu!"); return; }
        Alert konfirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Yakin ingin menghapus tanaman ini?", ButtonType.YES, ButtonType.NO);
        konfirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                tanamanService.hapusTanaman(selectedId);
                loadData();
                clearForm();
                showInfo("Tanaman berhasil dihapus!");
            }
        });
    }

    @FXML
    public void handleInfoObat() {
        if (selectedId == -1) { showAlert("Pilih tanaman terlebih dahulu!"); return; }
        TanamanDTO row = tblTanaman.getSelectionModel().getSelectedItem();
        if (row == null) return;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info Penggunaan Obat");
        alert.setHeaderText(row.getNama() + " (" + row.getJenis() + ")");
        alert.setContentText(tanamanService.getInfoObat(row));
        alert.showAndWait();
    }

    @FXML
    public void handleEstimasiPanen() {
        if (selectedId == -1) { showAlert("Pilih tanaman terlebih dahulu!"); return; }
        TanamanDTO row = tblTanaman.getSelectionModel().getSelectedItem();
        if (row == null) return;
        try {
            String hasil = tanamanService.getEstimasiPanen(row);
            showInfo(hasil);
        } catch (IllegalArgumentException ex) {
            showAlert(ex.getMessage());
        }
    }

    // ===== PRIVATE HELPERS =====

    private void loadData() {
        data.clear();
        data.addAll(tanamanService.getAllTanaman());
        tblTanaman.setItems(data);
    }

    private TanamanDTO buildDTOFromForm(int id) {
        int estimasi = 0;
        try {
            estimasi = Integer.parseInt(tfEstimasiHari.getText().trim());
        } catch (NumberFormatException ignored) {}

        TanamanDTO dto = new TanamanDTO();
        dto.setId(id);
        dto.setNama(tfNama.getText().trim());
        dto.setNamaLatin(tfNamaLatin.getText().trim());
        dto.setManfaat(taManfaat.getText().trim());
        dto.setPropertiTambahan(tfProperti.getText().trim());
        dto.setJenis(cmbJenis.getValue());
        dto.setTanggalTanam(dpTanggal.getValue());
        dto.setEstimasiHari(estimasi);
        return dto;
    }

    private void updateLabelProperti() {
        String jenis = cmbJenis.getValue();
        if ("Tanaman Rempah".equals(jenis))    lblProperti.setText("Aroma");
        else if ("Tanaman Daun".equals(jenis)) lblProperti.setText("Bentuk Daun");
        else                                   lblProperti.setText("Musim Berbuah");
    }

    private void updateStatusPreview() {
        LocalDate tgl      = dpTanggal.getValue();
        String    estimasiStr = tfEstimasiHari != null ? tfEstimasiHari.getText() : "";
        if (tgl == null) return;
        int estimasi = 0;
        try { estimasi = Integer.parseInt(estimasiStr); } catch (NumberFormatException ignored) {}
        if (estimasi <= 0) { lblStatusInfo.setText("Isi estimasi hari untuk preview status"); return; }
        com.toga.model.StatusTanaman status =
                com.toga.model.Tanaman.hitungStatus(tgl, estimasi);
        lblStatusInfo.setText("Status otomatis: " + status.name());
    }

    private void pasangFilterHurufSpasi(TextField tf) {
        tf.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty() && !newVal.matches("[a-zA-Z ]*"))
                tf.setText(oldVal);
        });
    }

    private void clearForm() {
        tfNama.clear(); tfNamaLatin.clear();
        taManfaat.clear(); tfProperti.clear();
        tfEstimasiHari.clear();
        cmbJenis.setValue("Tanaman Rempah");
        dpTanggal.setValue(LocalDate.now());
        selectedId = -1;
        lblStatusInfo.setText("Isi estimasi hari untuk preview status");
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
