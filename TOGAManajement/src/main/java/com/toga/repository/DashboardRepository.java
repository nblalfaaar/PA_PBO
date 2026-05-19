package com.toga.repository;

import com.toga.dto.DashboardDTO;
import java.util.List;

public interface DashboardRepository {
    int countTotalTanaman();
    int countTotalPengguna();
    int countJadwalHariIni();
    int countByJenisTanaman(String jenis);
    List<DashboardDTO.MendekatiPanenRow> findMendekatiPanen(int maxHari);
}