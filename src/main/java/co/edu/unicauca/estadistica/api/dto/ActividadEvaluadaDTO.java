package co.edu.unicauca.estadistica.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActividadEvaluadaDTO {
    private String nombreActividad;
    private Double fuente1;
    private Double fuente2;
}
