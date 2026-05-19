package com.toga.dto;

public class LaporanDTO {


    public static class PanenRow {
        private final String namaTanaman;
        private final String namaPengguna;
        private final String tanggal;
        private final int hasil;

        public PanenRow(String namaTanaman, String namaPengguna, String tanggal, int hasil) {
            this.namaTanaman = namaTanaman;
            this.namaPengguna = namaPengguna;
            this.tanggal = tanggal;
            this.hasil = hasil;
        }
        @SuppressWarnings("unused")
        public String getNamaTanaman() { return namaTanaman; }
        @SuppressWarnings("unused")
        public String getNamaPengguna() { return namaPengguna; }
        public String getTanggal() { return tanggal; }
        @SuppressWarnings("unused")
        public int getHasil() { return hasil; }
    }

    public static class PerawatanRow {
        private final String namaTanaman;
        private final String jenisPerawatan;
        private final String tanggal;
        private final String status;

        public PerawatanRow(String namaTanaman, String jenisPerawatan, String tanggal, String status) {
            this.namaTanaman = namaTanaman;
            this.jenisPerawatan = jenisPerawatan;
            this.tanggal = tanggal;
            this.status = status;
        }
        @SuppressWarnings("unused")
        public String getNamaTanaman() { return namaTanaman; }
        @SuppressWarnings("unused")
        public String getJenisPerawatan() { return jenisPerawatan; }
        public String getTanggal() { return tanggal; }
        public String getStatus() { return status; }
    }

    public static class SummaryData {
        private final int totalPanen;
        private final int totalPerawatan;
        private final int penggunaAktif;

        public SummaryData(int totalPanen, int totalPerawatan, int penggunaAktif) {
            this.totalPanen = totalPanen;
            this.totalPerawatan = totalPerawatan;
            this.penggunaAktif = penggunaAktif;
        }

        public int getTotalPanen() { return totalPanen; }
        public int getTotalPerawatan() { return totalPerawatan; }
        public int getPenggunaAktif() { return penggunaAktif; }
    }
}