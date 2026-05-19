package com.toga.service.impl;

import com.toga.dto.PanenDTO;
import com.toga.model.StatusTanaman;
import com.toga.model.Tanaman;
import com.toga.repository.PanenRepository;
import com.toga.repository.TanamanRepository;
import com.toga.service.PanenService;

import java.time.LocalDate;
import java.util.List;

public class PanenServiceImpl implements PanenService {

    private final PanenRepository    panenRepository;
    private final TanamanRepository  tanamanRepository;

    public PanenServiceImpl(PanenRepository panenRepository,
                            TanamanRepository tanamanRepository) {
        this.panenRepository   = panenRepository;
        this.tanamanRepository = tanamanRepository;
    }

    @Override
    public List<PanenDTO> getAllPanen() {
        return panenRepository.findAll();
    }

    @Override
    public void catatPanen(int tanamanId, int penggunaId, String keterangan,
                           LocalDate tanggalPanen, int hasilPanen) {
        if (keterangan == null || keterangan.isBlank())
            throw new IllegalArgumentException("Keterangan harus diisi!");
        if (hasilPanen <= 0)
            throw new IllegalArgumentException("Hasil panen harus lebih dari 0!");
        if (tanggalPanen == null)
            throw new IllegalArgumentException("Tanggal panen harus diisi!");

        panenRepository.save(tanamanId, penggunaId, keterangan, tanggalPanen, hasilPanen);
        tanamanRepository.updateStatus(tanamanId, StatusTanaman.SUDAH_DIPANEN.name());
    }

    @Override
    public void hapusPanen(int id) {
        int tanamanId = panenRepository.findTanamanIdByPanenId(id);
        panenRepository.delete(id);

        if (tanamanId != -1) {
            recalcStatus(tanamanId);
        }
    }

    private void recalcStatus(int tanamanId) {
        Tanaman t = tanamanRepository.findById(tanamanId);
        if (t == null) return;
        int estimasi = t.getEstimasiHari();
        StatusTanaman statusBaru = Tanaman.hitungStatus(t.getTanggalTanam(), estimasi);
        tanamanRepository.updateStatus(tanamanId, statusBaru.name());
    }
}
