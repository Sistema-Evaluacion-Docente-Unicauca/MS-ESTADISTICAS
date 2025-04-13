package co.edu.unicauca.estadistica.api.dto;

import lombok.Data;

@Data
public class UsuarioDetalleDTO {
    private Integer oidUsuarioDetalle;
    private String facultad;
    private String departamento;
    private String categoria;
    private String contratacion;
    private String dedicacion;
    private String estudios;
}