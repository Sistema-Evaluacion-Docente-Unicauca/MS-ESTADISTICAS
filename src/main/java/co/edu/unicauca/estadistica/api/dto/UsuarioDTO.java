package co.edu.unicauca.estadistica.api.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Integer oidUsuario;
    private UsuarioDetalleDTO usuarioDetalle;
    private EstadoUsuarioDTO estadoUsuario;
    private String identificacion;
    private String nombres;
    private String apellidos;
    private String username;
    private String correo;
}
