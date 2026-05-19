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
import java.util.logging.Level;
import java.util.logging.Logger;

public class TanamanController {

    private static final Logger LOGGER = Logger.getLogger(TanamanController.class.getName());

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

    @FXML private TableColumn<TanamanDTO, Void> colActionInfo;
    @FXML private TableColumn<TanamanDTO, Void> colActionEstimasi;

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

        tfEstimasiHari.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.matches("\\d*"))
                tfEstimasiHari.setText(oldVal);
        });

        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colJenis.setCellValueFactory(new PropertyValueFactory<>("jenis"));
        colNamaLatin.setCellValueFactory(new PropertyValueFactory<>("namaLatin"));
        colEstimasi.setCellValueFactory(new PropertyValueFactory<>("estimasiHari"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colActionInfo.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("🌿 Info");
            {
                btn.getStyleClass().add("table-action-btn-info");
                btn.setOnAction(e -> {
                    TanamanDTO tanaman = getTableView().getItems().get(getIndex());
                    handleInfoObat(tanaman);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(btn);
            }
        });

        colActionEstimasi.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("📅 Estimasi");
            {
                btn.getStyleClass().add("table-action-btn-estimasi");
                btn.setOnAction(e -> {
                    TanamanDTO tanaman = getTableView().getItems().get(getIndex());
                    handleEstimasiPanen(tanaman);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(btn);
            }
        });

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
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Gagal menambah tanaman", ex);
            showAlert("Terjadi kesalahan sistem. Silakan coba lagi.");
        }
    }

    @FXML
    public void handleUbah() {
        if (selectedId == -1) {
            showAlert("Pilih tanaman terlebih dahulu!");
            return;
        }
        try {
            TanamanDTO dto = buildDTOFromForm(selectedId);
            tanamanService.ubahTanaman(dto);
            loadData();
            clearForm();
            showInfo("Tanaman berhasil diubah! Status: " + dto.getStatus());
        } catch (IllegalArgumentException ex) {
            showAlert(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Gagal mengubah tanaman id: " + selectedId, ex);
            showAlert("Terjadi kesalahan sistem. Silakan coba lagi.");
        }
    }

    @FXML
    public void handleHapus() {
        if (selectedId == -1) {
            showAlert("Pilih tanaman terlebih dahulu!");
            return;
        }
        Alert konfirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Yakin ingin menghapus tanaman ini?", ButtonType.YES, ButtonType.NO);
        konfirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    tanamanService.hapusTanaman(selectedId);
                    loadData();
                    clearForm();
                    showInfo("Tanaman berhasil dihapus!");
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Gagal menghapus tanaman id: " + selectedId, ex);
                    showAlert("Gagal menghapus tanaman!");
                }
            }
        });
    }

    private void handleInfoObat(TanamanDTO tanaman) {
        if (tanaman == null) {
            showAlert("Pilih tanaman terlebih dahulu!");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info Penggunaan Obat");
        alert.setHeaderText(tanaman.getNama() + " (" + tanaman.getJenis() + ")");
        alert.setContentText(tanamanService.getInfoObat(tanaman));
        alert.showAndWait();
    }

    private void handleEstimasiPanen(TanamanDTO tanaman) {
        if (tanaman == null) {
            showAlert("Pilih tanaman terlebih dahulu!");
            return;
        }
        try {
            String hasil = tanamanService.getEstimasiPanen(tanaman);
            showInfo(hasil);
        } catch (IllegalArgumentException ex) {
            showAlert(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Gagal hitung estimasi panen", ex);
            showAlert("Terjadi kesalahan sistem. Silakan coba lagi.");
        }
    }

    private void loadData() {
        data.clear();
        try {
            data.addAll(tanamanService.getAllTanaman());
            tblTanaman.setItems(data);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Gagal load data tanaman", ex);
            showAlert("Gagal memuat data tanaman");
        }
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
        LocalDate tgl = dpTanggal.getValue();
        String estimasiStr = tfEstimasiHari != null ? tfEstimasiHari.getText() : "";
        if (tgl == null) return;
        int estimasi = 0;
        try { estimasi = Integer.parseInt(estimasiStr); } catch (NumberFormatException ignored) {}
        if (estimasi <= 0) {
            lblStatusInfo.setText("Isi estimasi hari untuk preview status");
            return;
        }
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