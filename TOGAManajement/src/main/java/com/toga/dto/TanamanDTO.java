package com.toga.dto;

import java.time.LocalDate;

public class TanamanDTO {
    private int       id;
    private String    nama;
    private String    namaLatin;
    private String    manfaat;
    private String    jenis;
    private String    propertiTambahan;
    private String    status;
    private LocalDate tanggalTanam;
    private int       estimasiHari;

    public TanamanDTO() {}

    public TanamanDTO(int id, String nama, String namaLatin, String manfaat,
                      String jenis, String propertiTambahan,
                      String status, LocalDate tanggalTanam, int estimasiHari) {
        this.id               = id;
        this.nama             = nama;
        this.namaLatin        = namaLatin;
        this.manfaat          = manfaat;
        this.jenis            = jenis;
        this.propertiTambahan = propertiTambahan;
        this.status           = status;
        this.tanggalTanam     = tanggalTanam;
        this.estimasiHari     = estimasiHari;
    }

    public int       getId()                      { return id; }
    public void      setId(int id)                { this.id = id; }

    public String    getNama()                    { return nama; }
    public void      setNama(String v)            { this.nama = v; }

    public String    getNamaLatin()               { return namaLatin; }
    public void      setNamaLatin(String v)       { this.namaLatin = v; }

    public String    getManfaat()                 { return manfaat; }
    public void      setManfaat(String v)         { this.manfaat = v; }

    public String    getJenis()                   { return jenis; }
    public void      setJenis(String v)           { this.jenis = v; }

    public String    getPropertiTambahan()        { return propertiTambahan; }
    public void      setPropertiTambahan(String v){ this.propertiTambahan = v; }

    public String    getStatus()                  { return status; }
    public void      setStatus(String v)          { this.status = v; }

    public LocalDate getTanggalTanam()            { return tanggalTanam; }
    public void      setTanggalTanam(LocalDate v) { this.tanggalTanam = v; }

    public int       getEstimasiHari()            { return estimasiHari; }
    public void      setEstimasiHari(int v)       { this.estimasiHari = v; }
}
