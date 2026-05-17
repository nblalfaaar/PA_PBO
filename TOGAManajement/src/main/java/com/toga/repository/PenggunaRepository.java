package com.toga.repository;

import com.toga.model.Pengguna;
import java.util.List;

public interface PenggunaRepository {
    List<Pengguna> findAll();
    Pengguna findById(int id);
    void save(Pengguna pengguna);
    void update(Pengguna pengguna);
    void delete(int id);
    boolean isNamaExists(int excludeId, String nama);
    boolean isDuplikat(int excludeId, String nama, String alamat);
}
