package co.edu.unicauca.estadistica.api.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeriodoRankingDTO {
    private Integer oidPeriodo;
    private String nombre;
    private List<DepartamentoRankingDTO> departamentos;
}
