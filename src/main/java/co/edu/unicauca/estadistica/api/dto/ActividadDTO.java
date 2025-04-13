package co.edu.unicauca.estadistica.api.dto;

import java.util.List;
import lombok.Data;

@Data
public class ActividadDTO {
    private Integer oidActividad;
    private String nombreActividad;
    private TipoActividadDTO tipoActividad;
    private Integer oidEvaluado;
    private EvaluadorDTO evaluador;
    private List<FuenteDTO> fuentes;
    private List<AtributoDTO> atributos;
    private String departamento;
}
