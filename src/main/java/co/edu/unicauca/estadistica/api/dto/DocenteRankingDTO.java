package co.edu.unicauca.estadistica.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocenteRankingDTO {
    private Integer oidUsuario;
    private String nombre;
    private String identificacion;
    private Double calificacion;
}
