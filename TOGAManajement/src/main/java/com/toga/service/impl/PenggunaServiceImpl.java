package com.toga.service.impl;

import com.toga.dto.PenggunaDTO;
import com.toga.model.Pengguna;
import com.toga.repository.PenggunaRepository;
import com.toga.service.PenggunaService;

import java.util.ArrayList;
import java.util.List;

public class PenggunaServiceImpl implements PenggunaService {

    private final PenggunaRepository penggunaRepository;

    private static final String REGEX_NAMA   = "[a-zA-Z ]+";
    private static final String REGEX_ALAMAT = "[a-zA-Z0-9 .]+";

    public PenggunaServiceImpl(PenggunaRepository penggunaRepository) {
        this.penggunaRepository = penggunaRepository;
    }

    @Override
    public List<PenggunaDTO> getAllPengguna() {
        List<Pengguna> list   = penggunaRepository.findAll();
        List<PenggunaDTO> result = new ArrayList<>();
        for (Pengguna p : list) {
            result.add(new PenggunaDTO(p.getId(), p.getNama(), p.getAlamat()));
        }
        return result;
    }

    @Override
    public void tambahPengguna(PenggunaDTO dto) {
        validasiDTO(dto);

        if (penggunaRepository.isNamaExists(-1, dto.getNama())) {
            throw new IllegalArgumentException(
                    "Nama pengguna \"" + dto.getNama() + "\" sudah terdaftar!\nGunakan nama yang berbeda.");
        }
        if (penggunaRepository.isDuplikat(-1, dto.getNama(), dto.getAlamat())) {
            throw new IllegalArgumentException("Data pengguna sudah ada! Tidak dapat menambahkan duplikat.");
        }

        Pengguna pengguna = new Pengguna(dto.getNama(), dto.getAlamat());
        penggunaRepository.save(pengguna);
    }

    @Override
    public void ubahPengguna(PenggunaDTO dto) {
        validasiDTO(dto);

        if (penggunaRepository.isNamaExists(dto.getId(), dto.getNama())) {
            throw new IllegalArgumentException(
                    "Nama pengguna \"" + dto.getNama() + "\" sudah terdaftar!\nGunakan nama yang berbeda.");
        }
        if (penggunaRepository.isDuplikat(dto.getId(), dto.getNama(), dto.getAlamat())) {
            throw new IllegalArgumentException("Data pengguna sudah ada! Tidak dapat menyimpan duplikat.");
        }

        Pengguna existing = penggunaRepository.findById(dto.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Pengguna tidak ditemukan!");
        }

        existing.setNama(dto.getNama());
        existing.setAlamat(dto.getAlamat());

        penggunaRepository.update(existing);
    }

    @Override
    public void hapusPengguna(int id) {
        penggunaRepository.delete(id);
    }

    private void validasiDTO(PenggunaDTO dto) {
        if (dto.getNama() == null || dto.getNama().isBlank())
            throw new IllegalArgumentException("Nama harus diisi!");
        if (dto.getAlamat() == null || dto.getAlamat().isBlank())
            throw new IllegalArgumentException("Alamat harus diisi!");
        if (!dto.getNama().matches(REGEX_NAMA))
            throw new IllegalArgumentException("Nama pengguna hanya boleh berisi huruf dan spasi!");
        if (!dto.getAlamat().matches(REGEX_ALAMAT))
            throw new IllegalArgumentException("Alamat hanya boleh berisi huruf, angka, spasi, dan titik!");
    }
}
