package com.toga.repository.impl;

import com.toga.config.DBConnection;
import com.toga.model.*;
import com.toga.repository.TanamanRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TanamanRepositoryImpl implements TanamanRepository {

    @Override
    public List<Tanaman> findAll() {
        List<Tanaman> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT * FROM tanaman ORDER BY nama");
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public Tanaman findById(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM tanaman WHERE id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public void save(Tanaman tanaman) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO tanaman "
                    + "(nama, nama_latin, manfaat, jenis, properti_tambahan, "
                    + " status, tanggal_tanam, estimasi_hari) "
                    + "VALUES (?,?,?,?,?,?,?,?)");
            ps.setString(1, tanaman.getNama());
            ps.setString(2, tanaman.getNamaLatin());
            ps.setString(3, tanaman.getManfaat());
            ps.setString(4, tanaman.getJenis());
            ps.setString(5, tanaman.getPropertiTambahan());
            ps.setString(6, tanaman.getStatus().name());
            ps.setDate(7, Date.valueOf(tanaman.getTanggalTanam()));
            ps.setInt(8, tanaman.getEstimasiHari());
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public void update(Tanaman tanaman) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE tanaman "
                    + "SET nama=?, nama_latin=?, manfaat=?, jenis=?, "
                    + "    properti_tambahan=?, status=?, tanggal_tanam=?, estimasi_hari=? "
                    + "WHERE id=?");
            ps.setString(1, tanaman.getNama());
            ps.setString(2, tanaman.getNamaLatin());
            ps.setString(3, tanaman.getManfaat());
            ps.setString(4, tanaman.getJenis());
            ps.setString(5, tanaman.getPropertiTambahan());
            ps.setString(6, tanaman.getStatus().name());
            ps.setDate(7, Date.valueOf(tanaman.getTanggalTanam()));
            ps.setInt(8, tanaman.getEstimasiHari());
            ps.setInt(9, tanaman.getId());
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public void delete(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM tanaman WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public boolean isDuplikat(int excludeId, String nama, String namaLatin,
                               String manfaat, String jenis,
                               String properti, LocalDate tanggal, int estimasiHari) {
        String sql =
                "SELECT COUNT(*) FROM tanaman "
                + "WHERE LOWER(TRIM(nama))              = LOWER(TRIM(?)) "
                + "  AND LOWER(TRIM(nama_latin))        = LOWER(TRIM(?)) "
                + "  AND LOWER(TRIM(manfaat))           = LOWER(TRIM(?)) "
                + "  AND jenis                          = ? "
                + "  AND LOWER(TRIM(properti_tambahan)) = LOWER(TRIM(?)) "
                + "  AND tanggal_tanam                  = ? "
                + "  AND estimasi_hari                  = ? "
                + "  AND id                            != ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, namaLatin);
            ps.setString(3, manfaat);
            ps.setString(4, jenis);
            ps.setString(5, properti);
            ps.setDate(6, Date.valueOf(tanggal));
            ps.setInt(7, estimasiHari);
            ps.setInt(8, excludeId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public String findStatusById(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT status FROM tanaman WHERE id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("status");
        } catch (Exception e) { e.printStackTrace(); }
        return "";
    }

    @Override
    public void updateStatus(int id, String status) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE tanaman SET status=? WHERE id=?");
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ===== PRIVATE HELPER =====

    private Tanaman mapRow(ResultSet rs) throws SQLException {
        LocalDate     tgl      = rs.getDate("tanggal_tanam").toLocalDate();
        StatusTanaman st       = StatusTanaman.valueOf(rs.getString("status"));
        String        jenis    = rs.getString("jenis");
        String        nama     = rs.getString("nama");
        String        latin    = rs.getString("nama_latin");
        String        manfaat  = rs.getString("manfaat");
        String        properti = rs.getString("properti_tambahan");
        int           estimasi = rs.getInt("estimasi_hari");

        Tanaman t = switch (jenis) {
            case "Tanaman Rempah" -> new TanamanRempah(nama, latin, manfaat, properti, st, tgl, estimasi);
            case "Tanaman Daun"   -> new TanamanDaun(nama, latin, manfaat, properti, st, tgl, estimasi);
            default               -> new TanamanBuah(nama, latin, manfaat, properti, st, tgl, estimasi);
        };

        t.setId(rs.getInt("id"));
        return t;
    }
}
