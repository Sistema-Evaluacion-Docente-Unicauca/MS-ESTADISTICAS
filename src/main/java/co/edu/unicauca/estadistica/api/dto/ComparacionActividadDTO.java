package co.edu.unicauca.estadistica.api.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComparacionActividadDTO {
    private UsuarioDTO docente;
    private List<EvaluacionDepartamentoDTO> evaluacionesPorDepartamento;
}
