package com.pmk.freeplayer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "enrichment_results")
data class EnrichmentResultEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Long = 0,
	val cancionId: Long,
	val fecha: Long,
	val exitoso: Boolean,
	val datosActualizados: Boolean,
	val letraEncontrada: Boolean,
	val nivelCoincidencia: Float?,
	val error: String?
)