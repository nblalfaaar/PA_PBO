package com.toga.controller;

import com.toga.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PenggunaController {

    @FXML private TextField tfNama;
    @FXML private TextField tfAlamat;

    @FXML private TableView<PenggunaRow>           tblPengguna;
    @FXML private TableColumn<PenggunaRow, String> colNama;
    @FXML private TableColumn<PenggunaRow, String> colAlamat;

    private final ObservableList<PenggunaRow> data = FXCollections.observableArrayList();
    private int selectedId = -1;

    private static final String REGEX_NAMA   = "[a-zA-Z ]+";          // huruf & spasi
    private static final String REGEX_ALAMAT = "[a-zA-Z0-9 .]+";      // huruf, angka, spasi, titik

    @FXML
    public void initialize() {
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colAlamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));

        // Filter real-time nama: hanya huruf & spasi
        tfNama.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty() && !newVal.matches("[a-zA-Z ]*")) {
                tfNama.setText(oldVal);
            }
        });

        // Filter real-time alamat: hanya huruf, angka, spasi, titik
        tfAlamat.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty() && !newVal.matches("[a-zA-Z0-9 .]*")) {
                tfAlamat.setText(oldVal);
            }
        });

        tblPengguna.setOnMouseClicked(e -> {
            PenggunaRow row = tblPengguna.getSelectionModel().getSelectedItem();
            if (row != null) {
                selectedId = row.getId();
                tfNama.setText(row.getNama());
                tfAlamat.setText(row.getAlamat());
            }
        });

        loadData();
    }

    @FXML
    public void handleTambah() {
        String nama   = tfNama.getText().trim();
        String alamat = tfAlamat.getText().trim();

        if (nama.isEmpty() || alamat.isEmpty()) {
            showAlert("Nama dan Alamat harus diisi!"); return;
        }
        if (!nama.matches(REGEX_NAMA)) {
            showAlert("Nama pengguna hanya boleh berisi huruf dan spasi!"); return;
        }
        if (!alamat.matches(REGEX_ALAMAT)) {
            showAlert("Alamat hanya boleh berisi huruf, angka, spasi, dan titik!"); return;
        }
        // Nama tidak boleh sama dengan pengguna lain
        if (isNamaExists(-1, nama)) {
            showAlert("Nama pengguna \"" + nama + "\" sudah terdaftar!\nGunakan nama yang berbeda."); return;
        }
        // Semua komponen tidak boleh sama (duplikat penuh)
        if (isDuplikatPengguna(-1, nama, alamat)) {
            showAlert("Data pengguna sudah ada! Tidak dapat menambahkan duplikat."); return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO pengguna (nama, alamat) VALUES (?, ?)");
            ps.setString(1, nama);
            ps.setString(2, alamat);
            ps.executeUpdate();
            loadData();
            clearForm();
            showInfo("Pengguna berhasil ditambahkan!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleUbah() {
        if (selectedId == -1) { showAlert("Pilih pengguna terlebih dahulu!"); return; }

        String nama   = tfNama.getText().trim();
        String alamat = tfAlamat.getText().trim();

        if (nama.isEmpty() || alamat.isEmpty()) {
            showAlert("Nama dan Alamat harus diisi!"); return;
        }
        if (!nama.matches(REGEX_NAMA)) {
            showAlert("Nama pengguna hanya boleh berisi huruf dan spasi!"); return;
        }
        if (!alamat.matches(REGEX_ALAMAT)) {
            showAlert("Alamat hanya boleh berisi huruf, angka, spasi, dan titik!"); return;
        }
        // Cek nama unik (kecualikan diri sendiri)
        if (isNamaExists(selectedId, nama)) {
            showAlert("Nama pengguna \"" + nama + "\" sudah terdaftar!\nGunakan nama yang berbeda."); return;
        }
        // Cek duplikat penuh (kecualikan diri sendiri)
        if (isDuplikatPengguna(selectedId, nama, alamat)) {
            showAlert("Data pengguna sudah ada! Tidak dapat menyimpan duplikat."); return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE pengguna SET nama=?, alamat=? WHERE id=?");
            ps.setString(1, nama);
            ps.setString(2, alamat);
            ps.setInt(3, selectedId);
            ps.executeUpdate();
            loadData();
            clearForm();
            showInfo("Pengguna berhasil diubah!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleHapus() {
        if (selectedId == -1) { showAlert("Pilih pengguna terlebih dahulu!"); return; }

        Alert konfirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Yakin ingin menghapus pengguna ini?\nSemua catatan terkait akan ikut terhapus.",
                ButtonType.YES, ButtonType.NO);
        konfirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try (Connection conn = DBConnection.getConnection()) {
                    PreparedStatement ps = conn.prepareStatement(
                            "DELETE FROM pengguna WHERE id=?");
                    ps.setInt(1, selectedId);
                    ps.executeUpdate();
                    loadData();
                    clearForm();
                    showInfo("Pengguna berhasil dihapus!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isNamaExists(int excludeId, String nama) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM pengguna "
                            + "WHERE LOWER(TRIM(nama)) = LOWER(TRIM(?)) AND id != ?");
            ps.setString(1, nama);
            ps.setInt(2, excludeId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isDuplikatPengguna(int excludeId, String nama, String alamat) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM pengguna "
                            + "WHERE LOWER(TRIM(nama))   = LOWER(TRIM(?)) "
                            + "  AND LOWER(TRIM(alamat)) = LOWER(TRIM(?)) "
                            + "  AND id != ?");
            ps.setString(1, nama);
            ps.setString(2, alamat);
            ps.setInt(3, excludeId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void loadData() {
        data.clear();
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT * FROM pengguna ORDER BY nama");
            while (rs.next()) {
                data.add(new PenggunaRow(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("alamat")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tblPengguna.setItems(data);
    }

    private void clearForm() {
        tfNama.clear();
        tfAlamat.clear();
        selectedId = -1;
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    public static class PenggunaRow {
        private final int    id;
        private final String nama;
        private final String alamat;

        public PenggunaRow(int id, String nama, String alamat) {
            this.id     = id;
            this.nama   = nama;
            this.alamat = alamat;
        }

        public int    getId()     { return id; }
        public String getNama()   { return nama; }
        public String getAlamat() { return alamat; }
    }
}
