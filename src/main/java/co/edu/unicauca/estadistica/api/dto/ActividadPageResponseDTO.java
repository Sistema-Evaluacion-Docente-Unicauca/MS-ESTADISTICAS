package co.edu.unicauca.estadistica.api.dto;

import java.util.List;
import lombok.Data;

@Data
public class ActividadPageResponseDTO {
    private List<ActividadDTO> content;
    private int totalPages;
    private long totalElements;
    private int number;
}