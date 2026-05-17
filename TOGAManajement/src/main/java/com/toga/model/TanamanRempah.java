package com.toga.model;

import org.apache.commons.lang3.StringUtils;
import java.time.LocalDate;

public class TanamanRempah extends Tanaman implements ITanamanObat {
    private String aroma;

    public TanamanRempah(String nama, String namaLatin, String manfaat,
                         String aroma, StatusTanaman status,
                         LocalDate tanggalTanam, int estimasiHari) {
        super(nama, namaLatin, manfaat, status, tanggalTanam, estimasiHari);
        this.aroma = aroma;
    }

    @Override
    public String getJenis() {
        return "Tanaman Rempah";
    }

    @Override
    public String getPropertiTambahan() {
        return aroma;
    }

    public void setAroma(String aroma) {
        if (!StringUtils.isBlank(aroma)) this.aroma = aroma;
    }

    @Override
    public String getDeskripsiObat() {
        return "Bagian rimpang " + nama + " dengan aroma " + aroma
                + " digunakan " + getManfaat();
    }

    @Override
    public String getCaraPenggunaan() {
        return "Rimpang " + nama + " dapat direbus dan diminum air rebusannya, "
                + "atau ditumbuk dan dioleskan pada bagian yang sakit.";
    }
}