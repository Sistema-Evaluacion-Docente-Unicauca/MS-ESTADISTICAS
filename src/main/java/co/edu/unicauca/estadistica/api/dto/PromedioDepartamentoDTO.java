package co.edu.unicauca.estadistica.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PromedioDepartamentoDTO {
    private String departamento;
    private Double promedioGeneral;
}