package co.edu.unicauca.estadistica.api.service;

import java.util.List;

import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.EvolucionDepartamentoDTO;

public interface EvolucionDepartamentoService {
    ApiResponse<List<EvolucionDepartamentoDTO>> obtenerEvolucionPromedios(List<Integer> periodos, List<String> nombresDepartamentos);
}

