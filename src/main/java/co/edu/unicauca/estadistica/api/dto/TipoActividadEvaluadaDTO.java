package co.edu.unicauca.estadistica.api.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoActividadEvaluadaDTO {
    private String tipoActividad;
    private List<ActividadEvaluadaDTO> actividades;
}
