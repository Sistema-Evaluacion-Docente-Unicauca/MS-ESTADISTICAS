package co.edu.unicauca.estadistica.api.service;

import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.ComparacionActividadDTO;

public interface EstadisticaService {
    ApiResponse<ComparacionActividadDTO> obtenerComparacionPorDocente(Integer idEvaluado, Integer idPeriodo, String idTipoActividad);
}