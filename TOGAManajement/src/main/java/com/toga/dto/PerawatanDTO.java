package com.toga.dto;

import java.time.LocalDate;

public class PerawatanDTO {
    private int id;
    private int tanamanId;
    private final String namaTanaman;
    private final String jenisPerawatan;
    private LocalDate tanggal;
    private final boolean sudahDilakukan;
    private final String namaPetugas;

    public PerawatanDTO(int id, int tanamanId, String namaTanaman,
                        String jenisPerawatan, LocalDate tanggal,
                        boolean sudahDilakukan, String namaPetugas) {
        this.id             = id;
        this.tanamanId      = tanamanId;
        this.namaTanaman    = namaTanaman;
        this.jenisPerawatan = jenisPerawatan;
        this.tanggal        = tanggal;
        this.sudahDilakukan = sudahDilakukan;
        this.namaPetugas    = namaPetugas;
    }

    public int       getId()                    { return id; }
    public void      setId(int id)              { this.id = id; }

    public int       getTanamanId()             { return tanamanId; }
    public void      setTanamanId(int v)        { this.tanamanId = v; }

    @SuppressWarnings("unused")
    public String    getNamaTanaman()           { return namaTanaman; }

    public String    getJenisPerawatan()        { return jenisPerawatan; }

    public LocalDate getTanggal()               { return tanggal; }
    public void      setTanggal(LocalDate v)    { this.tanggal = v; }

    public boolean   isSudahDilakukan()         { return sudahDilakukan; }

    @SuppressWarnings("unused")
    public String    getNamaPetugas()           { return namaPetugas; }

    @SuppressWarnings("unused")
    public String getTanggalStr() {
        return tanggal != null ? tanggal.toString() : "-";
    }

    public String getStatus() {
        return sudahDilakukan ? "Selesai" : "Belum";
    }
}
