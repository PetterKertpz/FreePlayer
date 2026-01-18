package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Cancion
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
	// Gestión de cola (NUEVO)
	// ─────────────────────────────────────────────────────────────
	suspend fun establecerCola(canciones: List<Cancion>, indiceInicial: Int = 0)
	
	suspend fun agregarACola(cancion: Cancion)
	
	suspend fun agregarACola(canciones: List<Cancion>)
	
	suspend fun quitarDeCola(indice: Int)
	
	suspend fun moverEnCola(desde: Int, hasta: Int)
	
	suspend fun limpiarCola()
	
	// ─────────────────────────────────────────────────────────────
	// Navegación (NUEVO)
	// ─────────────────────────────────────────────────────────────
	suspend fun irASiguiente(): Cancion?
	
	suspend fun irAAnterior(): Cancion?
	
	suspend fun irAIndice(indice: Int): Cancion?
	
	// ─────────────────────────────────────────────────────────────
	// Configuración de reproducción
	// ─────────────────────────────────────────────────────────────
	fun obtenerModoRepeticion(): Flow<ModoRepeticion>
	
	suspend fun setModoRepeticion(modo: ModoRepeticion)
	
	fun obtenerAleatorioActivado(): Flow<Boolean>
	
	suspend fun setAleatorioActivado(activado: Boolean)
}