package com.toga.repository.impl;

import com.toga.config.DBConnection;
import com.toga.dto.PanenDTO;
import com.toga.repository.PanenRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PanenRepositoryImpl implements PanenRepository {

    private static final Logger LOGGER = Logger.getLogger(PanenRepositoryImpl.class.getName());

    @Override
    public List<PanenDTO> findAll() {
        List<PanenDTO> list = new ArrayList<>();
        String sql = "SELECT cp.id, cp.tanaman_id, cp.pengguna_id, "
                + "       t.nama AS nama_tanaman, p.nama AS nama_pengguna, "
                + "       cp.tanggal_panen, cp.hasil_panen, cp.keterangan "
                + "FROM catatan_panen cp "
                + "JOIN tanaman  t ON cp.tanaman_id  = t.id "
                + "JOIN pengguna p ON cp.pengguna_id = p.id "
                + "ORDER BY cp.tanggal_panen DESC";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat findAll Panen");
                return list;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal findAll Panen", e);
        }
        return list;
    }

    @Override
    public void save(int tanamanId, int penggunaId, String keterangan,
                     LocalDate tanggalPanen, int hasilPanen) {
        String sql = "INSERT INTO catatan_panen "
                + "(tanaman_id, pengguna_id, keterangan, tanggal_panen, hasil_panen) "
                + "VALUES (?,?,?,?,?)";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat save Panen");
                return;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, tanamanId);
                ps.setInt(2, penggunaId);
                ps.setString(3, keterangan);
                ps.setDate(4, Date.valueOf(tanggalPanen));
                ps.setInt(5, hasilPanen);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal save Panen. TanamanId: " + tanamanId, e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM catatan_panen WHERE id=?";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat delete Panen");
                return;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal delete Panen. Id: " + id, e);
        }
    }

    @Override
    public int findTanamanIdByPanenId(int panenId) {
        String sql = "SELECT tanaman_id FROM catatan_panen WHERE id=?";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat findTanamanIdByPanenId");
                return -1;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, panenId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("tanaman_id");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal findTanamanIdByPanenId. PanenId: " + panenId, e);
        }
        return -1;
    }


    private PanenDTO mapRow(ResultSet rs) throws SQLException {
        return new PanenDTO(
                rs.getInt("id"),
                rs.getInt("tanaman_id"),
                rs.getInt("pengguna_id"),
                rs.getString("nama_tanaman"),
                rs.getString("nama_pengguna"),
                rs.getDate("tanggal_panen").toLocalDate(),
                rs.getInt("hasil_panen"),
                rs.getString("keterangan"));
    }
}