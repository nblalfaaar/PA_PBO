package com.toga.dto;

public class PenggunaDTO {
    private int    id;
    private String nama;
    private String alamat;

    public PenggunaDTO() {}

    public PenggunaDTO(int id, String nama, String alamat) {
        this.id     = id;
        this.nama   = nama;
        this.alamat = alamat;
    }

    public int    getId()           { return id; }
    public void   setId(int id)     { this.id = id; }

    public String getNama()         { return nama; }
    public void   setNama(String v) { this.nama = v; }

    public String getAlamat()          { return alamat; }
    public void   setAlamat(String v)  { this.alamat = v; }
}
