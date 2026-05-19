package com.toga.repository.impl;

import com.toga.config.DBConnection;
import com.toga.model.Pengguna;
import com.toga.repository.PenggunaRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PenggunaRepositoryImpl implements PenggunaRepository {

    private static final Logger LOGGER = Logger.getLogger(PenggunaRepositoryImpl.class.getName());

    @Override
    public List<Pengguna> findAll() {
        List<Pengguna> list = new ArrayList<>();
        String sql = "SELECT * FROM pengguna ORDER BY nama";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat findAll Pengguna");
                return list;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pengguna p = new Pengguna(rs.getString("nama"), rs.getString("alamat"));
                    p.setId(rs.getInt("id"));
                    list.add(p);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal findAll Pengguna", e);
        }
        return list;
    }

    @Override
    public Pengguna findById(int id) {
        String sql = "SELECT * FROM pengguna WHERE id=?";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat findById Pengguna");
                return null;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Pengguna p = new Pengguna(rs.getString("nama"), rs.getString("alamat"));
                        p.setId(rs.getInt("id"));
                        return p;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal findById Pengguna. Id: " + id, e);
        }
        return null;
    }

    @Override
    public void save(Pengguna pengguna) {
        String sql = "INSERT INTO pengguna (nama, alamat) VALUES (?,?)";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat save Pengguna");
                return;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, pengguna.getNama());
                ps.setString(2, pengguna.getAlamat());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal save Pengguna. Nama: " + pengguna.getNama(), e);
        }
    }

    @Override
    public void update(Pengguna pengguna) {
        String sql = "UPDATE pengguna SET nama=?, alamat=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat update Pengguna");
                return;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, pengguna.getNama());
                ps.setString(2, pengguna.getAlamat());
                ps.setInt(3, pengguna.getId());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal update Pengguna. Id: " + pengguna.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM pengguna WHERE id=?";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat delete Pengguna");
                return;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal delete Pengguna. Id: " + id, e);
        }
    }

    @Override
    public boolean isNamaExists(int excludeId, String nama) {
        String sql = "SELECT COUNT(*) FROM pengguna "
                + "WHERE LOWER(TRIM(nama)) = LOWER(TRIM(?)) AND id != ?";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat isNamaExists");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nama);
                ps.setInt(2, excludeId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal isNamaExists. Nama: " + nama + ", excludeId: " + excludeId, e);
        }
        return false;
    }

    @Override
    public boolean isDuplikat(int excludeId, String nama, String alamat) {
        String sql = "SELECT COUNT(*) FROM pengguna "
                + "WHERE LOWER(TRIM(nama))   = LOWER(TRIM(?)) "
                + "  AND LOWER(TRIM(alamat)) = LOWER(TRIM(?)) "
                + "  AND id != ?";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat isDuplikat");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nama);
                ps.setString(2, alamat);
                ps.setInt(3, excludeId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal isDuplikat. Nama: " + nama + ", Alamat: " + alamat, e);
        }
        return false;
    }
}