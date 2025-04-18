package co.edu.unicauca.estadistica.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsolidadoDTO {
    private Integer oidUsuario;
    private String nombreDocente;
    private String numeroIdentificacion;
    private Double calificacion;

    private Integer idPeriodoAcademico;
    private String periodoAcademico;

    private String departamento;
    private Integer idDepartamento;

    private Integer oidTipoActividad;
    private String nombreTipoActividad;
}
