package com.toga.controller;

import com.toga.dto.PenggunaDTO;
import com.toga.repository.impl.PenggunaRepositoryImpl;
import com.toga.service.PenggunaService;
import com.toga.service.impl.PenggunaServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class PenggunaController {

    @FXML private TextField tfNama;
    @FXML private TextField tfAlamat;

    @FXML private TableView<PenggunaDTO>           tblPengguna;
    @FXML private TableColumn<PenggunaDTO, String> colNama;
    @FXML private TableColumn<PenggunaDTO, String> colAlamat;

    private final PenggunaService penggunaService =
            new PenggunaServiceImpl(new PenggunaRepositoryImpl());

    private final ObservableList<PenggunaDTO> data = FXCollections.observableArrayList();
    private int selectedId = -1;

    @FXML
    public void initialize() {
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colAlamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));

        tfNama.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty() && !newVal.matches("[a-zA-Z ]*"))
                tfNama.setText(oldVal);
        });
        tfAlamat.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty() && !newVal.matches("[a-zA-Z0-9 .]*"))
                tfAlamat.setText(oldVal);
        });

        tblPengguna.setOnMouseClicked(e -> {
            PenggunaDTO row = tblPengguna.getSelectionModel().getSelectedItem();
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
        try {
            PenggunaDTO dto = new PenggunaDTO();
            dto.setNama(tfNama.getText().trim());
            dto.setAlamat(tfAlamat.getText().trim());
            penggunaService.tambahPengguna(dto);
            loadData();
            clearForm();
            showInfo("Pengguna berhasil ditambahkan!");
        } catch (IllegalArgumentException ex) {
            showAlert(ex.getMessage());
        }
    }

    @FXML
    public void handleUbah() {
        if (selectedId == -1) { showAlert("Pilih pengguna terlebih dahulu!"); return; }
        try {
            PenggunaDTO dto = new PenggunaDTO();
            dto.setId(selectedId);
            dto.setNama(tfNama.getText().trim());
            dto.setAlamat(tfAlamat.getText().trim());
            penggunaService.ubahPengguna(dto);
            loadData();
            clearForm();
            showInfo("Pengguna berhasil diubah!");
        } catch (IllegalArgumentException ex) {
            showAlert(ex.getMessage());
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
                penggunaService.hapusPengguna(selectedId);
                loadData();
                clearForm();
                showInfo("Pengguna berhasil dihapus!");
            }
        });
    }


    private void loadData() {
        data.clear();
        List<PenggunaDTO> list = penggunaService.getAllPengguna();
        data.addAll(list);
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
}
