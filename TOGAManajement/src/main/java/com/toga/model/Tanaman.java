package com.toga.model;

import org.apache.commons.lang3.StringUtils;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public abstract class Tanaman {
    private int id;
    protected String nama;
    private String namaLatin;
    private String manfaat;
    private StatusTanaman status;
    private LocalDate tanggalTanam;
    private int estimasiHari;

    public Tanaman(){
        this.nama = "nama tanaman";
        this.namaLatin = "nama latin";
        this.manfaat = "manfaat";
    }

    public Tanaman(String nama, String namaLatin, String manfaat,
                   StatusTanaman status, LocalDate tanggalTanam, int estimasiHari) {
        this.nama = nama;
        this.namaLatin = namaLatin;
        this.manfaat = manfaat;
        this.status = status;
        this.tanggalTanam = tanggalTanam;
        this.estimasiHari = estimasiHari;
    }

    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getNamaLatin() { return namaLatin; }
    public String getManfaat() { return manfaat; }
    public StatusTanaman getStatus() { return status; }
    public LocalDate getTanggalTanam() { return tanggalTanam; }
    public int getEstimasiHari() { return estimasiHari; }

    public void setId(int id) { this.id = id; }

    public void setNama(String nama) {
        if (!StringUtils.isBlank(nama)) this.nama = nama;
    }

    public void setNamaLatin(String namaLatin) {
        if (!StringUtils.isBlank(namaLatin)) this.namaLatin = namaLatin;
    }

    public void setManfaat(String manfaat) {
        if (!StringUtils.isBlank(manfaat)) this.manfaat = manfaat;
    }

    public void setStatus(StatusTanaman status) {
        if (status != null) this.status = status;
    }

    public void setTanggalTanam(LocalDate tanggalTanam) {
        if (tanggalTanam != null) this.tanggalTanam = tanggalTanam;
    }

    public void setEstimasiHari(int estimasiHari) {
        if (estimasiHari > 0) this.estimasiHari = estimasiHari;
    }

    public abstract String getJenis();
    public abstract String getPropertiTambahan();

    public int getSisaHariPanen() {
        if (this.tanggalTanam == null) return estimasiHari;
        long sudahBerlalu = ChronoUnit.DAYS.between(this.tanggalTanam, LocalDate.now());
        return Math.max(estimasiHari - (int) sudahBerlalu, 0);
    }

    public static StatusTanaman hitungStatus(LocalDate tanggalTanam, int estimasiHari) {
        if (tanggalTanam == null) return StatusTanaman.BIBIT;
        long hari = ChronoUnit.DAYS.between(tanggalTanam, LocalDate.now());

        int batasBibit = (int) (estimasiHari * 0.25);
        int batasTumbuh = estimasiHari - 14;

        if (hari < 0) {
            return StatusTanaman.BIBIT;
        } else if (hari < batasBibit) {
            return StatusTanaman.BIBIT;
        } else if (batasTumbuh > batasBibit && hari < batasTumbuh) {
            return StatusTanaman.TUMBUH;
        } else {
            return StatusTanaman.SIAP_PANEN;
        }
    }
}