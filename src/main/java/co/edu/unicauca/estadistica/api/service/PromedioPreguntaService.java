package co.edu.unicauca.estadistica.api.service;

import java.util.List;

import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.PeriodoRankingDTO;

public interface PromedioPreguntaService {
    public ApiResponse<List<PeriodoRankingDTO>> obtenerRankingPreguntas(String periodos, String departamentos, String tiposActividad, String token);
}
