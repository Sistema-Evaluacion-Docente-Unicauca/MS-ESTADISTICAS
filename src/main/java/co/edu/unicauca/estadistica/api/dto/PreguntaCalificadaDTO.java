package co.edu.unicauca.estadistica.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreguntaCalificadaDTO {
    private Integer oidPregunta;
    private String texto;
    private Float calificacion;
}

