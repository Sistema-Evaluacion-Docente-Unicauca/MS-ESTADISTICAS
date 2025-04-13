package co.edu.unicauca.estadistica.api.controller;

import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.PromedioDepartamentalResponse;
import co.edu.unicauca.estadistica.api.service.PromedioDepartamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
@Tag(name = "Estadísticas")
public class PromedioDepartamentoController {

    private final PromedioDepartamentoService promedioDepartamentoService;

    @Operation(
        summary = "Promedio de Evaluación por Departamento",
        description = "Calcula el promedio de evaluación por departamento con base en los consolidados generados en un periodo académico. Si no se envía el ID del periodo, se toma el activo."
    )
    @GetMapping("/promedio-departamento")
    public ResponseEntity<ApiResponse<PromedioDepartamentalResponse>> obtenerPromedios(
            @Parameter(description = "ID del periodo académico. Opcional, por defecto toma el periodo activo")
            @RequestParam(value = "idPeriodo", required = false) Integer idPeriodo) {

        ApiResponse<PromedioDepartamentalResponse> response = promedioDepartamentoService.obtenerPromediosPorDepartamento(idPeriodo);

        return ResponseEntity.status(response.getCodigo()).body(response);
    }
}
