package com.toga.repository;

import com.toga.model.Tanaman;
import java.time.LocalDate;
import java.util.List;
import com.toga.dto.TanamanDTO;
import java.util.Map;

public interface TanamanRepository {
    List<Tanaman> findAll();
    Tanaman findById(int id);
    void save(Tanaman tanaman);
    void update(Tanaman tanaman);
    void delete(int id);
    boolean isDuplikat(int excludeId, String nama, String namaLatin,
                       String manfaat, String jenis,
                       String properti, LocalDate tanggal, int estimasiHari);
    String findStatusById(int id);
    void updateStatus(int id, String status);
    Map<String, Integer> getTanamanIdMap();
    List<TanamanDTO> findAllForCombo();
}
