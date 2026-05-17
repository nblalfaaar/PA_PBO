package com.toga.repository.impl;

import com.toga.config.DBConnection;
import com.toga.model.Pengguna;
import com.toga.repository.PenggunaRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PenggunaRepositoryImpl implements PenggunaRepository {

    @Override
    public List<Pengguna> findAll() {
        List<Pengguna> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT * FROM pengguna ORDER BY nama");
            while (rs.next()) {
                Pengguna p = new Pengguna(rs.getString("nama"), rs.getString("alamat"));
                p.setId(rs.getInt("id"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Pengguna findById(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM pengguna WHERE id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Pengguna p = new Pengguna(rs.getString("nama"), rs.getString("alamat"));
                p.setId(rs.getInt("id"));
                return p;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(Pengguna pengguna) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO pengguna (nama, alamat) VALUES (?,?)");
            ps.setString(1, pengguna.getNama());
            ps.setString(2, pengguna.getAlamat());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Pengguna pengguna) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE pengguna SET nama=?, alamat=? WHERE id=?");
            ps.setString(1, pengguna.getNama());
            ps.setString(2, pengguna.getAlamat());
            ps.setInt(3, pengguna.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM pengguna WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isNamaExists(int excludeId, String nama) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM pengguna "
                    + "WHERE LOWER(TRIM(nama)) = LOWER(TRIM(?)) AND id != ?");
            ps.setString(1, nama);
            ps.setInt(2, excludeId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isDuplikat(int excludeId, String nama, String alamat) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM pengguna "
                    + "WHERE LOWER(TRIM(nama))   = LOWER(TRIM(?)) "
                    + "  AND LOWER(TRIM(alamat)) = LOWER(TRIM(?)) "
                    + "  AND id != ?");
            ps.setString(1, nama);
            ps.setString(2, alamat);
            ps.setInt(3, excludeId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
