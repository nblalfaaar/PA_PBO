package com.toga.repository.impl;

import com.toga.config.DBConnection;
import com.toga.model.Tanaman;
import com.toga.model.TanamanRempah;
import com.toga.model.TanamanDaun;
import com.toga.model.TanamanBuah;
import com.toga.model.StatusTanaman;
import com.toga.repository.TanamanRepository;
import com.toga.dto.TanamanDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TanamanRepositoryImpl implements TanamanRepository {

    private static final Logger LOGGER = Logger.getLogger(TanamanRepositoryImpl.class.getName());

    @Override
    public List<Tanaman> findAll() {
        List<Tanaman> list = new ArrayList<>();
        String sql = "SELECT * FROM tanaman ORDER BY nama";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat findAll Tanaman");
                return list;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal findAll Tanaman", e);
        }
        return list;
    }

    @Override
    public Tanaman findById(int id) {
        String sql = "SELECT * FROM tanaman WHERE id=?";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat findById Tanaman");
                return null;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapRow(rs);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal findById Tanaman. Id: " + id, e);
        }
        return null;
    }

    @Override
    public void save(Tanaman tanaman) {
        String sql = "INSERT INTO tanaman "
                + "(nama, nama_latin, manfaat, jenis, properti_tambahan, "
                + " status, tanggal_tanam, estimasi_hari) "
                + "VALUES (?,?,?,?,?,?,?,?)";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat save Tanaman");
                return;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, tanaman.getNama());
                ps.setString(2, tanaman.getNamaLatin());
                ps.setString(3, tanaman.getManfaat());
                ps.setString(4, tanaman.getJenis());
                ps.setString(5, tanaman.getPropertiTambahan());
                ps.setString(6, tanaman.getStatus().name());
                ps.setDate(7, Date.valueOf(tanaman.getTanggalTanam()));
                ps.setInt(8, tanaman.getEstimasiHari());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal save Tanaman. Nama: " + tanaman.getNama(), e);
        }
    }

    @Override
    public void update(Tanaman tanaman) {
        String sql = "UPDATE tanaman "
                + "SET nama=?, nama_latin=?, manfaat=?, jenis=?, "
                + "    properti_tambahan=?, status=?, tanggal_tanam=?, estimasi_hari=? "
                + "WHERE id=?";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat update Tanaman");
                return;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal update Tanaman. Id: " + tanaman.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM tanaman WHERE id=?";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat delete Tanaman");
                return;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal delete Tanaman. Id: " + id, e);
        }
    }

    @Override
    public boolean isDuplikat(int excludeId, String nama, String namaLatin,
                              String manfaat, String jenis,
                              String properti, LocalDate tanggal, int estimasiHari) {
        String sql = "SELECT COUNT(*) FROM tanaman "
                + "WHERE LOWER(TRIM(nama))              = LOWER(TRIM(?)) "
                + "  AND LOWER(TRIM(nama_latin))        = LOWER(TRIM(?)) "
                + "  AND LOWER(TRIM(manfaat))           = LOWER(TRIM(?)) "
                + "  AND jenis                          = ? "
                + "  AND LOWER(TRIM(properti_tambahan)) = LOWER(TRIM(?)) "
                + "  AND tanggal_tanam                  = ? "
                + "  AND estimasi_hari                  = ? "
                + "  AND id                            != ?";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat isDuplikat");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nama);
                ps.setString(2, namaLatin);
                ps.setString(3, manfaat);
                ps.setString(4, jenis);
                ps.setString(5, properti);
                ps.setDate(6, Date.valueOf(tanggal));
                ps.setInt(7, estimasiHari);
                ps.setInt(8, excludeId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal isDuplikat. Nama: " + nama, e);
        }
        return false;
    }

    @Override
    public String findStatusById(int id) {
        String sql = "SELECT status FROM tanaman WHERE id=?";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat findStatusById");
                return "";
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("status");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal findStatusById. Id: " + id, e);
        }
        return "";
    }

    @Override
    public void updateStatus(int id, String status) {
        String sql = "UPDATE tanaman SET status=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat updateStatus");
                return;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal updateStatus. Id: " + id + ", Status: " + status, e);
        }
    }

    @Override
    public Map<String, Integer> getTanamanIdMap() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT id, nama FROM tanaman ORDER BY nama";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat getTanamanIdMap");
                return map;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("nama"), rs.getInt("id"));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal getTanamanIdMap", e);
        }
        return map;
    }

    @Override
    public List<TanamanDTO> findAllForCombo() {
        List<TanamanDTO> list = new ArrayList<>();
        String sql = "SELECT id, nama, nama_latin, manfaat, jenis, properti_tambahan, status, tanggal_tanam, estimasi_hari "
                + "FROM tanaman WHERE status != 'SUDAH_DIPANEN' ORDER BY nama";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Koneksi database NULL saat findAllForCombo");
                return list;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TanamanDTO dto = new TanamanDTO();
                    dto.setId(rs.getInt("id"));
                    dto.setNama(rs.getString("nama"));
                    dto.setNamaLatin(rs.getString("nama_latin"));
                    dto.setManfaat(rs.getString("manfaat"));
                    dto.setJenis(rs.getString("jenis"));
                    dto.setPropertiTambahan(rs.getString("properti_tambahan"));
                    dto.setStatus(rs.getString("status"));
                    dto.setTanggalTanam(rs.getDate("tanggal_tanam").toLocalDate());
                    dto.setEstimasiHari(rs.getInt("estimasi_hari"));
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal findAllForCombo", e);
        }
        return list;
    }

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