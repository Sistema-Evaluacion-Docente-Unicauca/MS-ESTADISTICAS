package co.edu.unicauca.estadistica.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EvolucionPeriodoDTO {
    private String periodo;
    private Double promedioConsolidado;
}
