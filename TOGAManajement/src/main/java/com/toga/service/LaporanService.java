package com.toga.service;

import com.toga.dto.LaporanDTO;
import java.time.LocalDate;
import java.util.List;

public interface LaporanService {
    List<LaporanDTO.PanenRow> getPanenByDateRange(LocalDate dari, LocalDate sampai);
    List<LaporanDTO.PerawatanRow> getPerawatanByDateRange(LocalDate dari, LocalDate sampai);
    LaporanDTO.SummaryData getSummaryByDateRange(LocalDate dari, LocalDate sampai);
}