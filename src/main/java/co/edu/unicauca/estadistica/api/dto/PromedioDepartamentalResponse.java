package co.edu.unicauca.estadistica.api.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PromedioDepartamentalResponse {
    private List<PromedioDepartamentoDTO> promediosPorDepartamento;
}
