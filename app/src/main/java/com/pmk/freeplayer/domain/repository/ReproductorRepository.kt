package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.ColaReproduccion
import com.pmk.freeplayer.domain.model.Duracion
import com.pmk.freeplayer.domain.model.config.ModoRepeticion
import kotlinx.coroutines.flow.Flow

interface ReproductorRepository {
	
	// ─────────────────────────────────────────────────────────────
	// 💾 PERSISTENCIA (Guardar estado entre sesiones)
	// ─────────────────────────────────────────────────────────────
	suspend fun guardarEstadoSesion(
		cancionId: Long?,
		posicionMs: Long,
		colaIds: List<Long>,
		indiceActual: Int
	)
	
	// Recuperar la última sesión al abrir la app
	fun obtenerUltimaCancionId(): Flow<Long?>
	fun obtenerUltimaPosicion(): Flow<Long>
	fun obtenerUltimoIndice(): Flow<Int>
	fun obtenerUltimaColaIds(): Flow<List<Long>>
	
	// ─────────────────────────────────────────────────────────────
	// 🎵 GESTIÓN DE LA COLA ACTIVA (En Memoria)
	// ─────────────────────────────────────────────────────────────
	fun obtenerColaActual(): Flow<ColaReproduccion>
	
	suspend fun establecerCola(canciones: List<Song>, indiceInicial: Int = 0)
	
	suspend fun actualizarIndiceActual(nuevoIndice: Int)
	suspend fun actualizarCola(canciones: List<Song>, indiceActual: Int? = null)
	
	
	suspend fun agregarACola(canciones: List<Song>)
	
	suspend fun agregarAlFinal(song: Song)
	
	suspend fun agregarAcontinuacion(song: Song) // "Play Next"
	
	suspend fun eliminarDeCola(indice: Int)
	
	suspend fun moverEnCola(desde: Int, hasta: Int)
	
	suspend fun limpiarCola()
	
	// ─────────────────────────────────────────────────────────────
	// 🎛️ CONFIGURACIÓN DE REPRODUCCIÓN
	// ─────────────────────────────────────────────────────────────
	fun obtenerModoRepeticion(): Flow<ModoRepeticion>
	suspend fun setModoRepeticion(modo: ModoRepeticion)
	
	fun obtenerAleatorioActivado(): Flow<Boolean>
	suspend fun setAleatorioActivado(activado: Boolean)
	
	// ─────────────────────────────────────────────────────────────
	// 🎯 NAVEGACIÓN EN LA COLA
	// ─────────────────────────────────────────────────────────────
	suspend fun irASiguiente(): Song?
	suspend fun irAAnterior(): Song?
	suspend fun irAIndice(indice: Int): Song?
	
	// ─────────────────────────────────────────────────────────────
	// 📊 ESTADO ACTUAL
	// ─────────────────────────────────────────────────────────────
	fun obtenerCancionActual(): Flow<Song?>
	fun obtenerIndiceActual(): Flow<Int>
	fun obtenerPosicionActual(): Flow<Duracion>
	fun estaReproduciendo(): Flow<Boolean>
	fun obtenerEstadoReproduccion(): Flow<com.pmk.freeplayer.domain.model.EstadoReproduccion>
	
	// ─────────────────────────────────────────────────────────────
	// 🔀 ALEATORIO (SHUFFLE)
	// ─────────────────────────────────────────────────────────────
	suspend fun mezclarCola()
	suspend fun restaurarOrdenOriginal()
	fun tieneOrdenOriginal(): Flow<Boolean>
}