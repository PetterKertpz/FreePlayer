package com.pmk.freeplayer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_results")
data class ScanResultEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Long = 0,
	val fecha: Long,
	val archivosDetectados: Int,
	val cancionesNuevas: Int,
	val cancionesActualizadas: Int,
	val duplicadosIgnorados: Int,
	val archivosEliminados: Int,
	val errores: Int,
	val tiempoMs: Long
)