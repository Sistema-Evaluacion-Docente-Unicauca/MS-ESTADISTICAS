package co.edu.unicauca.estadistica.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EvolucionDepartamentoDTO {
    private String departamento;
    private List<EvolucionPeriodoDTO> evolucion;
}
