package co.edu.unicauca.estadistica.api.service;

import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.PromedioDepartamentalResponse;

public interface PromedioDepartamentoService {
    ApiResponse<PromedioDepartamentalResponse> obtenerPromediosPorDepartamento(Integer idPeriodo, String token);
}
