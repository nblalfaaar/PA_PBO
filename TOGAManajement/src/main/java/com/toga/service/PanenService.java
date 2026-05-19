package com.toga.service;

import com.toga.dto.PanenDTO;
import java.time.LocalDate;
import java.util.List;

public interface PanenService {
    List<PanenDTO> getAllPanen();
    void catatPanen(int tanamanId, int penggunaId, String keterangan,
                    LocalDate tanggalPanen, int hasilPanen);
    void hapusPanen(int id);
}
