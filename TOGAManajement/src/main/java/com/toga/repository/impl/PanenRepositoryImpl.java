package com.toga.repository.impl;

import com.toga.config.DBConnection;
import com.toga.dto.PanenDTO;
import com.toga.repository.PanenRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PanenRepositoryImpl implements PanenRepository {

    @Override
    public List<PanenDTO> findAll() {
        List<PanenDTO> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT cp.id, cp.tanaman_id, cp.pengguna_id, "
                    + "       t.nama AS nama_tanaman, p.nama AS nama_pengguna, "
                    + "       cp.tanggal_panen, cp.hasil_panen, cp.keterangan "
                    + "FROM catatan_panen cp "
                    + "JOIN tanaman  t ON cp.tanaman_id  = t.id "
                    + "JOIN pengguna p ON cp.pengguna_id = p.id "
                    + "ORDER BY cp.tanggal_panen DESC");
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void save(int tanamanId, int penggunaId, String keterangan,
                     LocalDate tanggalPanen, String hasilPanen) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO catatan_panen "
                    + "(tanaman_id, pengguna_id, keterangan, tanggal_panen, hasil_panen) "
                    + "VALUES (?,?,?,?,?)");
            ps.setInt(1, tanamanId);
            ps.setInt(2, penggunaId);
            ps.setString(3, keterangan);
            ps.setDate(4, Date.valueOf(tanggalPanen));
            ps.setString(5, hasilPanen);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM catatan_panen WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int findTanamanIdByPanenId(int panenId) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT tanaman_id FROM catatan_panen WHERE id=?");
            ps.setInt(1, panenId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("tanaman_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // ===== PRIVATE HELPER =====

    private PanenDTO mapRow(ResultSet rs) throws SQLException {
        return new PanenDTO(
                rs.getInt("id"),
                rs.getInt("tanaman_id"),
                rs.getInt("pengguna_id"),
                rs.getString("nama_tanaman"),
                rs.getString("nama_pengguna"),
                rs.getDate("tanggal_panen").toLocalDate(),
                rs.getString("hasil_panen"),
                rs.getString("keterangan"));
    }
}
