package co.edu.unicauca.estadistica.api.dto;

import lombok.Data;

@Data
public class ConsolidadoDTO {
    private String departamento;
    private Double calificacion;
    private String periodoAcademico;
    private Integer idPeriodoAcademico;
    private Integer idDepartamento;
}
