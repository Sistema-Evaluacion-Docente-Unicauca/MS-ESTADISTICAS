package co.edu.unicauca.estadistica.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import co.edu.unicauca.estadistica.api.client.ActividadDocenteClient;
import co.edu.unicauca.estadistica.api.client.TipoActividadClient;
import co.edu.unicauca.estadistica.api.client.UsuarioDocenteClient;
import co.edu.unicauca.estadistica.api.dto.ActividadDTO;
import co.edu.unicauca.estadistica.api.dto.ActividadEvaluadaDTO;
import co.edu.unicauca.estadistica.api.dto.ActividadEvaluadaWrapper;
import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.AtributoDTO;
import co.edu.unicauca.estadistica.api.dto.ComparacionActividadDTO;
import co.edu.unicauca.estadistica.api.dto.EvaluacionDepartamentoDTO;
import co.edu.unicauca.estadistica.api.dto.FuenteDTO;
import co.edu.unicauca.estadistica.api.dto.TipoActividadDTO;
import co.edu.unicauca.estadistica.api.dto.TipoActividadEvaluadaDTO;
import co.edu.unicauca.estadistica.api.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class EstadisticaServiceImpl implements EstadisticaService {

    private final ActividadDocenteClient actividadClient;
    private final UsuarioDocenteClient usuarioClient;
    private final TipoActividadClient tipoActividadClient;
    private static final Logger logger = LoggerFactory.getLogger(EstadisticaServiceImpl.class);

    @Override
    public ApiResponse<ComparacionActividadDTO> obtenerComparacionPorDocente(Integer idEvaluado, Integer idPeriodo,
            List<Integer> idTipoActividad) {

        if (idEvaluado == null) {
            return new ApiResponse<>(400, "El parámetro idEvaluado es obligatorio.", null);
        }
        // 1. Obtener todas las actividades del docente
        List<ActividadDTO> actividades = actividadClient.obtenerActividadesPorEvaluado(idEvaluado, idPeriodo);
        logger.debug("📦 Actividades recibidas para idEvaluado {}: {}", idEvaluado, actividades.size());

        if (actividades.isEmpty()) {
            return new ApiResponse<>(404, "No se encontraron actividades para el docente", null);
        }

        // 2. Obtener información del docente
        UsuarioDTO docente = usuarioClient.obtenerUsuarioPorId(idEvaluado);
        if (docente == null) {
            return new ApiResponse<>(404, "Docente no encontrado", null);
        }

        // 3. Si se recibe idTipoActividad, obtener su nombre y filtrar actividades
        if (idTipoActividad != null && !idTipoActividad.isEmpty()) {

            List<Integer> tiposValidos = new ArrayList<>();
            for (Integer tipoId : idTipoActividad) {
                TipoActividadDTO tipo = tipoActividadClient.obtenerPorId(tipoId);
                if (tipo == null) {
                    logger.warn("⚠️ Tipo de actividad con ID {} no encontrado", tipoId);
                    continue;
                }
                tiposValidos.add(tipoId);
            }

            if (tiposValidos.isEmpty()) {
                return new ApiResponse<>(404, "Ningún tipo de actividad válido encontrado", null);
            }

            actividades = actividades.stream()
                    .filter(a -> a.getTipoActividad() != null &&
                            tiposValidos.contains(a.getTipoActividad().getOidTipoActividad()))
                    .toList();

            logger.debug("🔍 Actividades luego de filtrar por tipos {}: {}", tiposValidos, actividades.size());
        }

        // 4. Construir y agrupar actividades evaluadas
        List<ActividadEvaluadaWrapper> wrappers = actividades.stream()
                .map(act -> {
                    String departamento = docente.getUsuarioDetalle() != null
                            ? docente.getUsuarioDetalle().getDepartamento()
                            : null;

                    String tipoActividad = act.getTipoActividad() != null ? act.getTipoActividad().getNombre() : null;

                    if (departamento == null || tipoActividad == null) {
                        logger.warn("❗ Actividad omitida (departamento o tipoActividad nulo). ID: {}",
                                act.getOidActividad());
                        return null;
                    }

                    String nombreActividad = act.getAtributos() != null
                            ? act.getAtributos().stream().filter(attr -> "ACTIVIDAD".equals(attr.getCodigoAtributo()))
                                    .map(AtributoDTO::getValor).findFirst().orElse(act.getNombreActividad())
                            : act.getNombreActividad();

                    if (nombreActividad == null) {
                        logger.warn("❌ Actividad sin nombre. ID: {}", act.getOidActividad());
                        return null;
                    }

                    Double fuente1 = act.getFuentes().stream()
                            .filter(f -> "1".equals(f.getTipoFuente()) && f.getCalificacion() != null)
                            .map(FuenteDTO::getCalificacion).findFirst().orElse(null);

                    Double fuente2 = act.getFuentes().stream()
                            .filter(f -> "2".equals(f.getTipoFuente()) && f.getCalificacion() != null)
                            .map(FuenteDTO::getCalificacion).findFirst().orElse(null);

                    return new ActividadEvaluadaWrapper(departamento, tipoActividad,
                            new ActividadEvaluadaDTO(nombreActividad, fuente1, fuente2));
                })
                .filter(Objects::nonNull)
                .toList();

        // 5. Agrupamiento por departamento → tipoActividad
        Map<String, Map<String, List<ActividadEvaluadaDTO>>> agrupado = wrappers.stream()
                .collect(Collectors.groupingBy(ActividadEvaluadaWrapper::departamento,
                        Collectors.groupingBy(ActividadEvaluadaWrapper::tipoActividad,
                                Collectors.mapping(ActividadEvaluadaWrapper::actividad, Collectors.toList()))));

        // 6. Convertir a DTO de respuesta
        List<EvaluacionDepartamentoDTO> evaluaciones = agrupado.entrySet().stream()
                .map(entry -> new EvaluacionDepartamentoDTO(entry.getKey(),
                        entry.getValue().entrySet().stream()
                                .map(tipoEntry -> new TipoActividadEvaluadaDTO(tipoEntry.getKey(),
                                        tipoEntry.getValue()))
                                .toList()))
                .toList();

        ComparacionActividadDTO comparacion = new ComparacionActividadDTO(docente, evaluaciones);
        return new ApiResponse<>(200, "Comparación generada correctamente", comparacion);
    }
}
