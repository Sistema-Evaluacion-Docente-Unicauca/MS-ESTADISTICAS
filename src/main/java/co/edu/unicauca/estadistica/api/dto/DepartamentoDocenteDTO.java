package co.edu.unicauca.estadistica.api.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartamentoDocenteDTO {
    private String nombre;
    private List<DocenteRankingDTO> docentes;
}
