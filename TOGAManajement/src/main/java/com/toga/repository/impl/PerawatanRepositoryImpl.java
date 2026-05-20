package com.toga.repository.impl;

import com.toga.config.DBConnection;
import com.toga.dto.PerawatanDTO;
import com.toga.repository.PerawatanRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PerawatanRepositoryImpl implements PerawatanRepository {

    private static final Logger LOGGER = Logger.getLogger(PerawatanRepositoryImpl.class.getName());

    @Override
    public List<PerawatanDTO> findAll() {
        List<PerawatanDTO> list = new ArrayList<>();
        String sql = "SELECT j.id, j.tanaman_id, t.nama AS nama_tanaman, "
                + "       j.jenis_perawatan, j.tanggal, j.sudah_dilakukan, "
                + "       p.nama AS nama_petugas "
                + "FROM jadwal_perawatan j "
                + "JOIN tanaman t ON j.tanaman_id = t.id "
                + "LEFT JOIN catatan_perawatan cp ON cp.jadwal_id = j.id "
                + "LEFT JOIN pengguna p ON cp.pengguna_id = p.id "
                + "ORDER BY j.tanggal DESC";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat findAll Perawatan");
                return list;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
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
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal findAll Perawatan", e);
        }
        return list;
    }

    @Override
    public PerawatanDTO findById(int id) {
        String sql = "SELECT j.id, j.tanaman_id, t.nama AS nama_tanaman, "
                + "       j.jenis_perawatan, j.tanggal, j.sudah_dilakukan "
                + "FROM jadwal_perawatan j "
                + "JOIN tanaman t ON j.tanaman_id = t.id "
                + "WHERE j.id=?";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat findById Perawatan");
                return null;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
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
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal findById Perawatan. Id: " + id, e);
        }
        return null;
    }

    @Override
    public void save(int tanamanId, String jenisPerawatan, LocalDate tanggal) {
        String sql = "INSERT INTO jadwal_perawatan "
                + "(tanaman_id, jenis_perawatan, tanggal, sudah_dilakukan) "
                + "VALUES (?,?,?,?)";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat save Perawatan");
                return;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, tanamanId);
                ps.setString(2, jenisPerawatan);
                ps.setDate(3, Date.valueOf(tanggal));
                ps.setBoolean(4, false);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal save Perawatan. TanamanId: " + tanamanId, e);
        }
    }

    @Override
    public void tandaiSelesai(int jadwalId) {
        String sql = "UPDATE jadwal_perawatan SET sudah_dilakukan=TRUE WHERE id=?";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat tandaiSelesai");
                return;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, jadwalId);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal tandaiSelesai. JadwalId: " + jadwalId, e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM jadwal_perawatan WHERE id=?";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat delete Perawatan");
                return;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal delete Perawatan. Id: " + id, e);
        }
    }

    @Override
    public void saveCatatanPerawatan(int jadwalId, int tanamanId, int penggunaId,
                                     String keterangan, LocalDate tanggal) {
        String sql = "INSERT INTO catatan_perawatan (jadwal_id, pengguna_id, keterangan) VALUES (?,?,?)";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat saveCatatanPerawatan");
                return;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, jadwalId);
                ps.setInt(2, penggunaId);
                ps.setString(3, keterangan);
                ps.executeUpdate();
                LOGGER.info("Catatan perawatan tersimpan untuk jadwalId: " + jadwalId);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal saveCatatanPerawatan. JadwalId: " + jadwalId, e);
        }
    }

    @Override
    public int countBelumHariIni() {
        String sql = "SELECT COUNT(*) FROM jadwal_perawatan "
                + "WHERE tanggal = CURDATE() AND sudah_dilakukan = FALSE";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat countBelumHariIni");
                return 0;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal countBelumHariIni", e);
        }
        return 0;
    }

    @Override
    public boolean isJadwalExist(int tanamanId, String jenisPerawatan, LocalDate tanggal) {
        String sql = "SELECT COUNT(*) FROM jadwal_perawatan "
                + "WHERE tanaman_id = ? AND jenis_perawatan = ? AND tanggal = ?";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat isJadwalExist");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, tanamanId);
                ps.setString(2, jenisPerawatan);
                ps.setDate(3, Date.valueOf(tanggal));
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal isJadwalExist. TanamanId: " + tanamanId +
                    ", Jenis: " + jenisPerawatan + ", Tanggal: " + tanggal, e);
            return false;
        }
    }
}