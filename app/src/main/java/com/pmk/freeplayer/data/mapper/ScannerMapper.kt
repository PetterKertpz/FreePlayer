package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.entity.CleaningResultEntity
import com.pmk.freeplayer.data.local.entity.EnrichmentResultEntity
import com.pmk.freeplayer.data.local.entity.ScanResultEntity
import com.pmk.freeplayer.domain.model.TrackDuration
import com.pmk.freeplayer.domain.model.scanner.CleaningResult
import com.pmk.freeplayer.domain.model.scanner.EnrichmentResult
import com.pmk.freeplayer.domain.model.scanner.ScanResult

fun ScanResultEntity.toDomain() = ScanResult(
	id = id,
	fecha = fecha,
	archivosDetectados = archivosDetectados,
	cancionesNuevas = cancionesNuevas,
	cancionesActualizadas = cancionesActualizadas,
	duplicadosIgnorados = duplicadosIgnorados,
	archivosEliminados = archivosEliminados,
	errores = errores,
	tiempoMs = TrackDuration(tiempoMs)
)

fun ScanResult.toEntity() = ScanResultEntity(
	id = id,
	fecha = fecha,
	archivosDetectados = archivosDetectados,
	cancionesNuevas = cancionesNuevas,
	cancionesActualizadas = cancionesActualizadas,
	duplicadosIgnorados = duplicadosIgnorados,
	archivosEliminados = archivosEliminados,
	errores = errores,
	tiempoMs = tiempoMs.millis
)

fun CleaningResultEntity.toDomain() = CleaningResult(
	id = id,
	fecha = fecha,
	cancionesProcesadas = cancionesProcesadas,
	cancionesLimpiadas = cancionesLimpiadas,
	errores = errores,
	tiempoMs = TrackDuration(tiempoMs)
)

fun CleaningResult.toEntity() = CleaningResultEntity(
	id = id,
	fecha = fecha,
	cancionesProcesadas = cancionesProcesadas,
	cancionesLimpiadas = cancionesLimpiadas,
	errores = errores,
	tiempoMs = tiempoMs.millis
)

fun EnrichmentResultEntity.toDomain() = EnrichmentResult(
	id = id,
	cancionId = cancionId,
	fecha = fecha,
	exitoso = exitoso,
	datosActualizados = datosActualizados,
	letraEncontrada = letraEncontrada,
	nivelCoincidencia = nivelCoincidencia,
	error = error
)

fun EnrichmentResult.toEntity() = EnrichmentResultEntity(
	id = id,
	cancionId = cancionId,
	fecha = fecha,
	exitoso = exitoso,
	datosActualizados = datosActualizados,
	letraEncontrada = letraEncontrada,
	nivelCoincidencia = nivelCoincidencia,
	error = error
)