package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.ColaReproduccion
import com.pmk.freeplayer.domain.model.enums.ModoRepeticion
import kotlinx.coroutines.flow.Flow

interface ReproductorRepository {

  // ─────────────────────────────────────────────────────────────
  // Guardar/Restaurar estado
  // ─────────────────────────────────────────────────────────────
  suspend fun guardarEstado(
      cancionId: Long?,
      posicion: Long,
      colaIds: List<Long>,
      indiceActual: Int,
  )

  fun obtenerUltimaCancionId(): Flow<Long?>

  fun obtenerUltimaPosicion(): Flow<Long>

  fun obtenerUltimaCola(): Flow<ColaReproduccion>

  // ─────────────────────────────────────────────────────────────
  // Configuración de reproducción
  // ─────────────────────────────────────────────────────────────
  fun obtenerModoRepeticion(): Flow<ModoRepeticion>

  suspend fun setModoRepeticion(modo: ModoRepeticion)

  fun obtenerAleatorioActivado(): Flow<Boolean>

  suspend fun setAleatorioActivado(activado: Boolean)
}
