package com.toga.service.impl;

import com.toga.dto.PerawatanDTO;
import org.apache.commons.lang3.StringUtils;
import com.toga.repository.PerawatanRepository;
import com.toga.service.PerawatanService;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

public class PerawatanServiceImpl implements PerawatanService {

    private static final Logger LOGGER = Logger.getLogger(PerawatanServiceImpl.class.getName());

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

        if (perawatanRepository.isJadwalExist(tanamanId, jenisPerawatan, tanggal)) {
            throw new IllegalArgumentException("Jadwal perawatan dengan jenis '" + jenisPerawatan +
                    "' untuk tanaman ini pada tanggal " + tanggal + " sudah ada!");
        }

        perawatanRepository.save(tanamanId, jenisPerawatan, tanggal);
        LOGGER.info("Jadwal perawatan ditambahkan. TanamanId: " + tanamanId +
                ", Jenis: " + jenisPerawatan + ", Tanggal: " + tanggal);
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

        perawatanRepository.saveCatatanPerawatan(jadwalId, jadwal.getTanamanId(),
                penggunaId, keterangan, jadwal.getTanggal());

        LOGGER.info("Jadwal perawatan ditandai selesai. JadwalId: " + jadwalId +
                ", PenggunaId: " + penggunaId + ", Petugas: " + namaPengguna);
    }

    @Override
    public void hapusJadwal(int id) {
        perawatanRepository.delete(id);
        LOGGER.info("Jadwal perawatan dihapus. Id: " + id);
    }

    @Override
    public int countBelumHariIni() {
        return perawatanRepository.countBelumHariIni();
    }
}