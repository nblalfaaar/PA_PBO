package com.toga.repository;

import com.toga.dto.PerawatanDTO;
import java.time.LocalDate;
import java.util.List;

public interface PerawatanRepository {
    List<PerawatanDTO> findAll();
    PerawatanDTO findById(int id);
    void save(int tanamanId, String jenisPerawatan, LocalDate tanggal);
    void tandaiSelesai(int jadwalId);
    void delete(int id);
    void saveCatatanPerawatan(int tanamanId, int penggunaId,
                              String keterangan, LocalDate tanggal);
    int countBelumHariIni();
}
