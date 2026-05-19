package com.toga.service.impl;

import com.toga.dto.DashboardDTO;
import com.toga.repository.DashboardRepository;
import com.toga.service.DashboardService;
import javafx.collections.FXCollections;

public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardServiceImpl(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    @Override
    public DashboardDTO getDashboardData() {
        int totalTanaman = dashboardRepository.countTotalTanaman();
        int totalPengguna = dashboardRepository.countTotalPengguna();
        int jadwalHariIni = dashboardRepository.countJadwalHariIni();
        int jumlahRempah = dashboardRepository.countByJenisTanaman("Tanaman Rempah");
        int jumlahDaun = dashboardRepository.countByJenisTanaman("Tanaman Daun");
        int jumlahBuah = dashboardRepository.countByJenisTanaman("Tanaman Buah");

        var mendekatiPanenList = dashboardRepository.findMendekatiPanen(30);
        var mendekatiPanen = FXCollections.observableArrayList(mendekatiPanenList);

        int siapPanen = mendekatiPanen.size();

        return new DashboardDTO(totalTanaman, siapPanen, totalPengguna, jadwalHariIni,
                jumlahRempah, jumlahDaun, jumlahBuah, mendekatiPanen);
    }
}