package com.toga.model;

import org.apache.commons.lang3.StringUtils;
import java.time.LocalDate;

public class TanamanBuah extends Tanaman implements ITanamanObat {
    private String musimBerbuah;

    public TanamanBuah(String nama, String namaLatin, String manfaat,
                       String musimBerbuah, StatusTanaman status,
                       LocalDate tanggalTanam, int estimasiHari) {
        super(nama, namaLatin, manfaat, status, tanggalTanam, estimasiHari);
        this.musimBerbuah = musimBerbuah;
    }

    @Override
    public String getJenis() {
        return "Tanaman Buah";
    }

    @Override
    public String getPropertiTambahan() {
        return musimBerbuah;
    }

    public void setMusimBerbuah(String musimBerbuah) {
        if (!StringUtils.isBlank(musimBerbuah)) this.musimBerbuah = musimBerbuah;
    }

    @Override
    public String getDeskripsiObat() {
        return "Buah " + nama + " yang dipanen saat musim " + musimBerbuah
                + " digunakan " + getManfaat();
    }

    @Override
    public String getCaraPenggunaan() {
        return "Buah " + nama + " dapat dikonsumsi langsung saat matang, "
                + "atau diolah menjadi jus dan ekstrak untuk penggunaan obat.";
    }
}