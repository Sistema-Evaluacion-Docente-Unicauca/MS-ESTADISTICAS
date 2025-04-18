package co.edu.unicauca.estadistica.api.service;

import java.util.List;
import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.PeriodoDocenteDTO;

public interface RankingDocenteService {
    ApiResponse<List<PeriodoDocenteDTO>> obtenerRankingDocentes(String periodos, String departamentos);
}

