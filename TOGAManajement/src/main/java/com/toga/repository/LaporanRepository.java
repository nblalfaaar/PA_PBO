package com.toga.repository;

import com.toga.dto.LaporanDTO;
import java.time.LocalDate;
import java.util.List;

public interface LaporanRepository {
    List<LaporanDTO.PanenRow> findPanenByDateRange(LocalDate dari, LocalDate sampai);
    List<LaporanDTO.PerawatanRow> findPerawatanByDateRange(LocalDate dari, LocalDate sampai);
    LaporanDTO.SummaryData getSummaryByDateRange(LocalDate dari, LocalDate sampai);
}