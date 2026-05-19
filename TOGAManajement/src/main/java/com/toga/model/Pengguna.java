package com.toga.model;

import org.apache.commons.lang3.StringUtils;

public class Pengguna {
    private int    id;
    private String nama;
    private String alamat;

    public Pengguna(String nama, String alamat) {
        this.nama   = nama;
        this.alamat = alamat;
    }

    public int    getId()         { return id; }
    public void   setId(int id)   { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) {
        if (StringUtils.isBlank(nama)) {
            throw new IllegalArgumentException("Nama tidak boleh kosong!");
        }
        this.nama = nama;
    }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) {
        if (StringUtils.isBlank(alamat)) {
            throw new IllegalArgumentException("Alamat tidak boleh kosong!");
        }
        this.alamat = alamat;
    }

    @Override
    public String toString() { return nama; }
}
