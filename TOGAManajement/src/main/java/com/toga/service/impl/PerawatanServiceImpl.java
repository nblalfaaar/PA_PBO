package com.toga.service.impl;

import com.toga.dto.PerawatanDTO;
import org.apache.commons.lang3.StringUtils;
import com.toga.repository.PerawatanRepository;
import com.toga.service.PerawatanService;

import java.time.LocalDate;
import java.util.List;

public class PerawatanServiceImpl implements PerawatanService {

    private final PerawatanRepository perawatanRepository;

    public PerawatanServiceImpl(PerawatanRepository perawatanRepository) {
        this.perawatanRepository = perawatanRepository;
    }

    @Override
    public List<PerawatanDTO> getAllJadwal() {
        return perawatanRepository.findAll();
    }

    @Override
    public void tambahJadwal(int tanamanId, String jenisPerawatan, LocalDate tanggal) {
        if (jenisPerawatan == null || jenisPerawatan.isBlank())
            throw new IllegalArgumentException("Jenis perawatan harus diisi!");
        if (tanggal == null)
            throw new IllegalArgumentException("Tanggal harus diisi!");

        if (StringUtils.isBlank(jenisPerawatan))
            throw new IllegalArgumentException("Jenis perawatan harus diisi!");

        perawatanRepository.save(tanamanId, jenisPerawatan, tanggal);
    }

    @Override
    public void tandaiSelesai(int jadwalId, int penggunaId, String namaPengguna) {
        PerawatanDTO jadwal = perawatanRepository.findById(jadwalId);
        if (jadwal == null)
            throw new IllegalArgumentException("Jadwal tidak ditemukan!");
        if (jadwal.isSudahDilakukan())
            throw new IllegalArgumentException("Jadwal ini sudah ditandai selesai sebelumnya!");

        perawatanRepository.tandaiSelesai(jadwalId);

        String keterangan = jadwal.getJenisPerawatan() + " oleh " + namaPengguna;
        perawatanRepository.saveCatatanPerawatan(
                jadwal.getTanamanId(), penggunaId, keterangan, jadwal.getTanggal());
    }

    @Override
    public void hapusJadwal(int id) {
        perawatanRepository.delete(id);
    }

    @Override
    public int countBelumHariIni() {
        return perawatanRepository.countBelumHariIni();
    }
}
