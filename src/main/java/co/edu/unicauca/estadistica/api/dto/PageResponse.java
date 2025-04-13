package co.edu.unicauca.estadistica.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class PageResponse<T> {
    private List<T> content;
}
