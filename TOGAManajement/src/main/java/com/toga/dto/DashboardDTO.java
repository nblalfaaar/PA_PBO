package com.toga.dto;

import javafx.collections.ObservableList;

public class DashboardDTO {
    private final int totalTanaman;
    private final int siapPanen;
    private final int totalPengguna;
    private final int jadwalHariIni;
    private final int jumlahRempah;
    private final int jumlahDaun;
    private final int jumlahBuah;
    private final ObservableList<MendekatiPanenRow> mendekatiPanen;

    public DashboardDTO(int totalTanaman, int siapPanen, int totalPengguna,
                        int jadwalHariIni, int jumlahRempah, int jumlahDaun,
                        int jumlahBuah, ObservableList<MendekatiPanenRow> mendekatiPanen) {
        this.totalTanaman = totalTanaman;
        this.siapPanen = siapPanen;
        this.totalPengguna = totalPengguna;
        this.jadwalHariIni = jadwalHariIni;
        this.jumlahRempah = jumlahRempah;
        this.jumlahDaun = jumlahDaun;
        this.jumlahBuah = jumlahBuah;
        this.mendekatiPanen = mendekatiPanen;
    }

    public int getTotalTanaman() { return totalTanaman; }
    public int getSiapPanen() { return siapPanen; }
    public int getTotalPengguna() { return totalPengguna; }
    public int getJadwalHariIni() { return jadwalHariIni; }
    public int getJumlahRempah() { return jumlahRempah; }
    public int getJumlahDaun() { return jumlahDaun; }
    public int getJumlahBuah() { return jumlahBuah; }
    public ObservableList<MendekatiPanenRow> getMendekatiPanen() { return mendekatiPanen; }

    public static class MendekatiPanenRow {
        private final String nama;
        private final String sisa;

        public MendekatiPanenRow(String nama, String sisa) {
            this.nama = nama;
            this.sisa = sisa;
        }

        public String getNama() { return nama; }
        @SuppressWarnings("unused")
        public String getSisa() { return sisa; }
    }
}