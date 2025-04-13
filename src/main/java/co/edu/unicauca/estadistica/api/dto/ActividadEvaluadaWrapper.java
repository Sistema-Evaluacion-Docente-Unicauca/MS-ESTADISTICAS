package co.edu.unicauca.estadistica.api.dto;

public record ActividadEvaluadaWrapper(
    String departamento,
    String tipoActividad,
    ActividadEvaluadaDTO actividad
) {}
