package co.edu.unicauca.estadistica.api.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.EvolucionDepartamentoDTO;
import co.edu.unicauca.estadistica.api.service.EvolucionDepartamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
@Tag(name = "Estadísticas")
public class EvolucionDepartamentoController {

    private final EvolucionDepartamentoService evolucionService;

    @Operation(summary = "Evolución de promedio por departamento", description = "Retorna la evolución del promedio de consolidados agrupado por departamento para los periodos indicados.")
    @GetMapping("/evolucion-promedio")
    public ResponseEntity<ApiResponse<List<EvolucionDepartamentoDTO>>> obtenerEvolucion(
            @RequestParam(required = false) String periodos,
            @RequestParam(required = false) Integer idDepartamento) {
    
        if (periodos == null || periodos.isBlank()) {
            ApiResponse<List<EvolucionDepartamentoDTO>> error = new ApiResponse<>(400, "Debe especificar al menos un periodo académico.", List.of());
            return ResponseEntity.badRequest().body(error);
        }
    
        List<Integer> periodosAcademicos;
        try {
            periodosAcademicos = Arrays.stream(periodos.split(","))
                    .map(String::trim)
                    .filter(p -> !p.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            ApiResponse<List<EvolucionDepartamentoDTO>> error = new ApiResponse<>(400, "Los valores de periodo deben ser numéricos válidos.", List.of());
            return ResponseEntity.badRequest().body(error);
        }

        if (periodosAcademicos.isEmpty()) {
            ApiResponse<List<EvolucionDepartamentoDTO>> error = new ApiResponse<>(400, "Debe especificar al menos un periodo académico válido.", List.of());
            return ResponseEntity.badRequest().body(error);
        }
    
        ApiResponse<List<EvolucionDepartamentoDTO>> response = evolucionService.obtenerEvolucionPromedios(periodosAcademicos, idDepartamento);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }    
}