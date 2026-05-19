package com.toga.repository.impl;

import com.toga.config.DBConnection;
import com.toga.dto.DashboardDTO;
import com.toga.repository.DashboardRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DashboardRepositoryImpl implements DashboardRepository {

    private static final Logger LOGGER = Logger.getLogger(DashboardRepositoryImpl.class.getName());

    @Override
    public int countTotalTanaman() {
        String sql = "SELECT COUNT(*) FROM tanaman";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat countTotalTanaman");
                return 0;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal countTotalTanaman", e);
        }
        return 0;
    }

    @Override
    public int countTotalPengguna() {
        String sql = "SELECT COUNT(*) FROM pengguna";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat countTotalPengguna");
                return 0;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal countTotalPengguna", e);
        }
        return 0;
    }

    @Override
    public int countJadwalHariIni() {
        String sql = "SELECT COUNT(*) FROM jadwal_perawatan WHERE tanggal = CURDATE() AND sudah_dilakukan = FALSE";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat countJadwalHariIni");
                return 0;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal countJadwalHariIni", e);
        }
        return 0;
    }

    @Override
    public int countByJenisTanaman(String jenis) {
        String sql = "SELECT COUNT(*) FROM tanaman WHERE jenis = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat countByJenisTanaman");
                return 0;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, jenis);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal countByJenisTanaman: " + jenis, e);
        }
        return 0;
    }

    @Override
    public List<DashboardDTO.MendekatiPanenRow> findMendekatiPanen(int maxHari) {
        List<DashboardDTO.MendekatiPanenRow> list = new ArrayList<>();
        String sql = "SELECT nama, tanggal_tanam, estimasi_hari FROM tanaman WHERE status != 'SUDAH_DIPANEN'";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat findMendekatiPanen");
                return list;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    String nama = rs.getString("nama");
                    LocalDate tanam = rs.getDate("tanggal_tanam").toLocalDate();
                    int estimasi = rs.getInt("estimasi_hari");
                    LocalDate panen = tanam.plusDays(estimasi);
                    long sisa = ChronoUnit.DAYS.between(LocalDate.now(), panen);

                    if (sisa >= 0 && sisa <= maxHari) {
                        list.add(new DashboardDTO.MendekatiPanenRow(nama, sisa + " hari"));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal findMendekatiPanen, maxHari: " + maxHari, e);
        }
        return list;
    }
}