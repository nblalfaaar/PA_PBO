package com.toga.repository.impl;

import com.toga.config.DBConnection;
import com.toga.dto.LaporanDTO;
import com.toga.repository.LaporanRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LaporanRepositoryImpl implements LaporanRepository {

    private static final Logger LOGGER = Logger.getLogger(LaporanRepositoryImpl.class.getName());

    @Override
    public List<LaporanDTO.PanenRow> findPanenByDateRange(LocalDate dari, LocalDate sampai) {
        List<LaporanDTO.PanenRow> list = new ArrayList<>();
        String sql = "SELECT t.nama AS nama_tanaman, p.nama AS nama_pengguna, "
                + "cp.tanggal_panen, cp.hasil_panen "
                + "FROM catatan_panen cp "
                + "JOIN tanaman  t ON cp.tanaman_id  = t.id "
                + "JOIN pengguna p ON cp.pengguna_id = p.id "
                + "WHERE cp.tanggal_panen BETWEEN ? AND ? "
                + "ORDER BY cp.tanggal_panen DESC";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat findPanenByDateRange");
                return list;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDate(1, Date.valueOf(dari));
                ps.setDate(2, Date.valueOf(sampai));

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(new LaporanDTO.PanenRow(
                                rs.getString("nama_tanaman"),
                                rs.getString("nama_pengguna"),
                                rs.getDate("tanggal_panen").toLocalDate().toString(),
                                rs.getInt("hasil_panen")
                        ));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal findPanenByDateRange. Dari: " + dari + ", Sampai: " + sampai, e);
        }
        return list;
    }

    @Override
    public List<LaporanDTO.PerawatanRow> findPerawatanByDateRange(LocalDate dari, LocalDate sampai) {
        List<LaporanDTO.PerawatanRow> list = new ArrayList<>();
        String sql = "SELECT t.nama AS nama_tanaman, j.jenis_perawatan, "
                + "j.tanggal, j.sudah_dilakukan "
                + "FROM jadwal_perawatan j "
                + "JOIN tanaman t ON j.tanaman_id = t.id "
                + "WHERE j.tanggal BETWEEN ? AND ? "
                + "ORDER BY j.tanggal DESC";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat findPerawatanByDateRange");
                return list;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDate(1, Date.valueOf(dari));
                ps.setDate(2, Date.valueOf(sampai));

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(new LaporanDTO.PerawatanRow(
                                rs.getString("nama_tanaman"),
                                rs.getString("jenis_perawatan"),
                                rs.getDate("tanggal").toLocalDate().toString(),
                                rs.getBoolean("sudah_dilakukan") ? "Selesai" : "Belum"
                        ));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal findPerawatanByDateRange. Dari: " + dari + ", Sampai: " + sampai, e);
        }
        return list;
    }

    @Override
    public LaporanDTO.SummaryData getSummaryByDateRange(LocalDate dari, LocalDate sampai) {
        int totalPanen = 0;
        int totalPerawatan = 0;
        int penggunaAktif = 0;

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat getSummaryByDateRange");
                return new LaporanDTO.SummaryData(0, 0, 0);
            }

            String sql1 = "SELECT COUNT(*) FROM catatan_panen WHERE tanggal_panen BETWEEN ? AND ?";
            try (PreparedStatement ps1 = conn.prepareStatement(sql1)) {
                ps1.setDate(1, Date.valueOf(dari));
                ps1.setDate(2, Date.valueOf(sampai));
                try (ResultSet rs1 = ps1.executeQuery()) {
                    if (rs1.next()) totalPanen = rs1.getInt(1);
                }
            }

            String sql2 = "SELECT COUNT(*) FROM jadwal_perawatan WHERE tanggal BETWEEN ? AND ?";
            try (PreparedStatement ps2 = conn.prepareStatement(sql2)) {
                ps2.setDate(1, Date.valueOf(dari));
                ps2.setDate(2, Date.valueOf(sampai));
                try (ResultSet rs2 = ps2.executeQuery()) {
                    if (rs2.next()) totalPerawatan = rs2.getInt(1);
                }
            }

            String sql3 = "SELECT COUNT(DISTINCT pengguna_id) FROM catatan_panen "
                    + "WHERE tanggal_panen BETWEEN ? AND ?";
            try (PreparedStatement ps3 = conn.prepareStatement(sql3)) {
                ps3.setDate(1, Date.valueOf(dari));
                ps3.setDate(2, Date.valueOf(sampai));
                try (ResultSet rs3 = ps3.executeQuery()) {
                    if (rs3.next()) penggunaAktif = rs3.getInt(1);
                }
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal getSummaryByDateRange. Dari: " + dari + ", Sampai: " + sampai, e);
        }

        return new LaporanDTO.SummaryData(totalPanen, totalPerawatan, penggunaAktif);
    }
}