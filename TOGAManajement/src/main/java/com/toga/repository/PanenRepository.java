package com.toga.repository;

import com.toga.dto.PanenDTO;
import java.time.LocalDate;
import java.util.List;

public interface PanenRepository {
    List<PanenDTO> findAll();
    void save(int tanamanId, int penggunaId, String keterangan,
              LocalDate tanggalPanen, String hasilPanen);
    void delete(int id);
    int findTanamanIdByPanenId(int panenId);
}
