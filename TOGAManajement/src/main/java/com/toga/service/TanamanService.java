package com.toga.service;

import com.toga.dto.TanamanDTO;
import java.util.List;

public interface TanamanService {
    List<TanamanDTO> getAllTanaman();
    void tambahTanaman(TanamanDTO dto);
    boolean ubahTanaman(TanamanDTO dto);
    void hapusTanaman(int id);
    String getInfoObat(TanamanDTO dto);
    String getEstimasiPanen(TanamanDTO dto);
    List<TanamanDTO> getAllTanamanForCombo();
    void updateAllStatusOtomatis();
    List<TanamanDTO> getAllTanamanForPerawatan();
}