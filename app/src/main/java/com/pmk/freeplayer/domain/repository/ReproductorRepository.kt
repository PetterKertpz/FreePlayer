package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Cancion
import com.pmk.freeplayer.domain.model.ColaReproduccion
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
	fun obtenerUltimaPosicion(): Flow<Long>
	fun obtenerUltimoIndice(): Flow<Int>
	fun obtenerUltimaColaIds(): Flow<List<Long>> // Solo IDs para no cargar todo el contenido
	
	// ─────────────────────────────────────────────────────────────
	// 🎵 GESTIÓN DE LA COLA ACTIVA (En Memoria)
	// ─────────────────────────────────────────────────────────────
	// Observa la cola actual en tiempo real
	fun obtenerColaActual(): Flow<ColaReproduccion>
	
	suspend fun establecerCola(canciones: List<Cancion>, indiceInicial: Int = 0)
	
	suspend fun actualizarIndiceActual(nuevoIndice: Int)
	
	suspend fun agregarACola(canciones: List<Cancion>)
	
	suspend fun agregarAlFinal(cancion: Cancion)
	
	suspend fun agregarAcontinuacion(cancion: Cancion) // "Play Next"
	
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
}