package com.toga.service;

import com.toga.dto.PerawatanDTO;
import java.time.LocalDate;
import java.util.List;

public interface PerawatanService {
    List<PerawatanDTO> getAllJadwal();
    void tambahJadwal(int tanamanId, String jenisPerawatan, LocalDate tanggal);
    void tandaiSelesai(int jadwalId, int penggunaId, String namaPengguna);
    void hapusJadwal(int id);
    int countBelumHariIni();
}
