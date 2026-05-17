package com.toga.service;

import com.toga.dto.PenggunaDTO;
import java.util.List;

public interface PenggunaService {
    List<PenggunaDTO> getAllPengguna();
    void tambahPengguna(PenggunaDTO dto);
    void ubahPengguna(PenggunaDTO dto);
    void hapusPengguna(int id);
}
