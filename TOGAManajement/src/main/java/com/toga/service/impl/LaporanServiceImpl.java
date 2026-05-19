package com.toga.service.impl;

import com.toga.dto.LaporanDTO;
import com.toga.repository.LaporanRepository;
import com.toga.service.LaporanService;
import java.time.LocalDate;
import java.util.List;

public class LaporanServiceImpl implements LaporanService {

    private final LaporanRepository laporanRepository;

    public LaporanServiceImpl(LaporanRepository laporanRepository) {
        this.laporanRepository = laporanRepository;
    }

    @Override
    public List<LaporanDTO.PanenRow> getPanenByDateRange(LocalDate dari, LocalDate sampai) {
        if (dari == null || sampai == null) {
            throw new IllegalArgumentException("Tanggal harus diisi!");
        }
        if (dari.isAfter(sampai)) {
            throw new IllegalArgumentException("Tanggal awal tidak boleh melebihi tanggal akhir!");
        }
        return laporanRepository.findPanenByDateRange(dari, sampai);
    }

    @Override
    public List<LaporanDTO.PerawatanRow> getPerawatanByDateRange(LocalDate dari, LocalDate sampai) {
        if (dari == null || sampai == null) {
            throw new IllegalArgumentException("Tanggal harus diisi!");
        }
        if (dari.isAfter(sampai)) {
            throw new IllegalArgumentException("Tanggal awal tidak boleh melebihi tanggal akhir!");
        }
        return laporanRepository.findPerawatanByDateRange(dari, sampai);
    }

    @Override
    public LaporanDTO.SummaryData getSummaryByDateRange(LocalDate dari, LocalDate sampai) {
        if (dari == null || sampai == null) {
            throw new IllegalArgumentException("Tanggal harus diisi!");
        }
        if (dari.isAfter(sampai)) {
            throw new IllegalArgumentException("Tanggal awal tidak boleh melebihi tanggal akhir!");
        }
        return laporanRepository.getSummaryByDateRange(dari, sampai);
    }
}