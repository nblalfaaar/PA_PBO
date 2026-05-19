package com.toga.dto;

import java.time.LocalDate;

public class PanenDTO {
    private int id;
    private int tanamanId;
    private int penggunaId;
    private final String namaTanaman;
    private final String namaPengguna;
    private final LocalDate tanggalPanen;
    private final int hasilPanen;
    private String keterangan;

    public PanenDTO(int id, int tanamanId, int penggunaId,
                    String namaTanaman, String namaPengguna,
                    LocalDate tanggalPanen, int hasilPanen, String keterangan) {
        this.id = id;
        this.tanamanId = tanamanId;
        this.penggunaId = penggunaId;
        this.namaTanaman = namaTanaman;
        this.namaPengguna = namaPengguna;
        this.tanggalPanen = tanggalPanen;
        this.hasilPanen = hasilPanen;
        this.keterangan = keterangan;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTanamanId() { return tanamanId; }
    public void setTanamanId(int v) { this.tanamanId = v; }

    public int getPenggunaId() { return penggunaId; }
    public void setPenggunaId(int v) { this.penggunaId = v; }

    public String getNamaTanaman() { return namaTanaman; }
    public String getNamaPengguna() { return namaPengguna; }
    public LocalDate getTanggalPanen() { return tanggalPanen; }
    public int getHasilPanen() { return hasilPanen; }
    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String v) { this.keterangan = v; }

    @SuppressWarnings("unused")
    public String getTanggalPanenStr() {
        return tanggalPanen != null ? tanggalPanen.toString() : "-";
    }

    @SuppressWarnings("unused")
    public String getHasilPanenStr() {
        return String.valueOf(hasilPanen);
    }
}