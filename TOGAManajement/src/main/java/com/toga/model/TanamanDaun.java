package com.toga.model;

import org.apache.commons.lang3.StringUtils;
import java.time.LocalDate;

public class TanamanDaun extends Tanaman implements ITanamanObat {
    private String bentukDaun;

    public TanamanDaun(String nama, String namaLatin, String manfaat,
                       String bentukDaun, StatusTanaman status,
                       LocalDate tanggalTanam, int estimasiHari) {
        super(nama, namaLatin, manfaat, status, tanggalTanam, estimasiHari);
        this.bentukDaun = bentukDaun;
    }

    @Override
    public String getJenis() {
        return "Tanaman Daun";
    }

    @Override
    public String getPropertiTambahan() {
        return bentukDaun;
    }

    public void setBentukDaun(String bentukDaun) {
        if (!StringUtils.isBlank(bentukDaun)) this.bentukDaun = bentukDaun;
    }

    @Override
    public String getDeskripsiObat() {
        return "Daun " + nama + " dengan bentuk " + bentukDaun
                + " digunakan " + getManfaat();
    }

    @Override
    public String getCaraPenggunaan() {
        return "Daun " + nama + " dapat direbus untuk diminum air rebusannya, "
                + "atau ditumbuk halus dan ditempelkan langsung pada kulit.";
    }
}