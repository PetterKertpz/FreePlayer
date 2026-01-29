package com.pmk.freeplayer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cleaning_results")
data class CleaningResultEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Long = 0,
	val fecha: Long,
	val cancionesProcesadas: Int,
	val cancionesLimpiadas: Int,
	val errores: Int,
	val tiempoMs: Long
)