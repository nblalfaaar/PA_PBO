package com.toga.repository.impl;

import com.toga.config.DBConnection;
import com.toga.dto.PerawatanDTO;
import com.toga.repository.PerawatanRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PerawatanRepositoryImpl implements PerawatanRepository {

    @Override
    public List<PerawatanDTO> findAll() {
        List<PerawatanDTO> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT j.id, j.tanaman_id, t.nama AS nama_tanaman, "
                    + "       j.jenis_perawatan, j.tanggal, j.sudah_dilakukan, "
                    + "       p.nama AS nama_petugas "
                    + "FROM jadwal_perawatan j "
                    + "JOIN tanaman t ON j.tanaman_id = t.id "
                    + "LEFT JOIN catatan_perawatan cp "
                    + "       ON cp.tanaman_id = j.tanaman_id "
                    + "      AND cp.tanggal    = j.tanggal "
                    + "LEFT JOIN pengguna p ON cp.pengguna_id = p.id "
                    + "ORDER BY j.tanggal DESC");
            while (rs.next()) {
                String petugas = rs.getString("nama_petugas");
                list.add(new PerawatanDTO(
                        rs.getInt("id"),
                        rs.getInt("tanaman_id"),
                        rs.getString("nama_tanaman"),
                        rs.getString("jenis_perawatan"),
                        rs.getDate("tanggal").toLocalDate(),
                        rs.getBoolean("sudah_dilakukan"),
                        petugas != null ? petugas : "-"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public PerawatanDTO findById(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT j.id, j.tanaman_id, t.nama AS nama_tanaman, "
                    + "       j.jenis_perawatan, j.tanggal, j.sudah_dilakukan "
                    + "FROM jadwal_perawatan j "
                    + "JOIN tanaman t ON j.tanaman_id = t.id "
                    + "WHERE j.id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new PerawatanDTO(
                        rs.getInt("id"),
                        rs.getInt("tanaman_id"),
                        rs.getString("nama_tanaman"),
                        rs.getString("jenis_perawatan"),
                        rs.getDate("tanggal").toLocalDate(),
                        rs.getBoolean("sudah_dilakukan"),
                        "-");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(int tanamanId, String jenisPerawatan, LocalDate tanggal) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO jadwal_perawatan "
                    + "(tanaman_id, jenis_perawatan, tanggal, sudah_dilakukan) "
                    + "VALUES (?,?,?,?)");
            ps.setInt(1, tanamanId);
            ps.setString(2, jenisPerawatan);
            ps.setDate(3, Date.valueOf(tanggal));
            ps.setBoolean(4, false);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tandaiSelesai(int jadwalId) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE jadwal_perawatan SET sudah_dilakukan=TRUE WHERE id=?");
            ps.setInt(1, jadwalId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM jadwal_perawatan WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveCatatanPerawatan(int tanamanId, int penggunaId,
                                     String keterangan, LocalDate tanggal) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO catatan_perawatan "
                    + "(tanaman_id, pengguna_id, keterangan, tanggal) "
                    + "VALUES (?,?,?,?)");
            ps.setInt(1, tanamanId);
            ps.setInt(2, penggunaId);
            ps.setString(3, keterangan);
            ps.setDate(4, Date.valueOf(tanggal));
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int countBelumHariIni() {
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT COUNT(*) FROM jadwal_perawatan "
                    + "WHERE tanggal = CURDATE() AND sudah_dilakukan = FALSE");
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
