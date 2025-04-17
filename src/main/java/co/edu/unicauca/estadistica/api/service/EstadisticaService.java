package co.edu.unicauca.estadistica.api.service;

import java.util.List;
import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.ComparacionActividadDTO;

public interface EstadisticaService {
    ApiResponse<ComparacionActividadDTO> obtenerComparacionPorDocente(Integer idEvaluado, Integer idPeriodo, List<Integer> idTipoActividad);
}