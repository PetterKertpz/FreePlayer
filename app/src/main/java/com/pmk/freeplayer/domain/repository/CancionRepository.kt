package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Cancion
import com.pmk.freeplayer.domain.model.EstadisticasBiblioteca
import com.pmk.freeplayer.domain.model.enums.EstadoCancion
import com.pmk.freeplayer.domain.model.enums.Genero
import com.pmk.freeplayer.domain.model.enums.TipoOrdenamiento
import kotlinx.coroutines.flow.Flow

interface CancionRepository {
	
	// ─────────────────────────────────────────────────────────────
	// Obtener canciones
	// ─────────────────────────────────────────────────────────────
	fun obtenerTodas(): Flow<List<Cancion>>
	
	fun obtenerPorId(id: Long): Flow<Cancion?>
	
	fun obtenerPorAlbum(albumId: Long): Flow<List<Cancion>>
	
	fun obtenerPorArtista(artista: String): Flow<List<Cancion>>
	
	fun obtenerPorGenero(genero: Genero): Flow<List<Cancion>>
	
	fun obtenerPorCarpeta(ruta: String): Flow<List<Cancion>>
	
	fun obtenerOrdenadas(ordenamiento: TipoOrdenamiento): Flow<List<Cancion>>
	
	// ─────────────────────────────────────────────────────────────
	// Búsqueda
	// ─────────────────────────────────────────────────────────────
	fun buscar(consulta: String): Flow<List<Cancion>>
	
	// ─────────────────────────────────────────────────────────────
	// Favoritos
	// ─────────────────────────────────────────────────────────────
	fun obtenerFavoritas(): Flow<List<Cancion>>
	
	suspend fun marcarComoFavorita(id: Long, esFavorita: Boolean)
	
	suspend fun alternarFavorita(id: Long)
	
	// ─────────────────────────────────────────────────────────────
	// Estadísticas de reproducción
	// ─────────────────────────────────────────────────────────────
	fun obtenerMasReproducidas(limite: Int = 50): Flow<List<Cancion>>
	
	fun obtenerReproducidasRecientemente(limite: Int = 50): Flow<List<Cancion>>
	
	fun obtenerAgregadasRecientemente(limite: Int = 50): Flow<List<Cancion>>
	
	suspend fun incrementarReproduccion(id: Long)
	
	suspend fun actualizarUltimaReproduccion(id: Long, timestamp: Long)
	
	// ─────────────────────────────────────────────────────────────
	// Estadísticas generales
	// ─────────────────────────────────────────────────────────────
	suspend fun obtenerCantidadTotal(): Int
	
	// ─────────────────────────────────────────────────────────────
	// Gestión por estados
	// ─────────────────────────────────────────────────────────────
	fun obtenerPorEstado(estado: EstadoCancion): Flow<List<Cancion>>
	
	fun obtenerCrudas(): Flow<List<Cancion>>
	
	fun obtenerLimpias(): Flow<List<Cancion>>
	
	fun obtenerEnriquecidas(): Flow<List<Cancion>>
	
	suspend fun contarPorEstado(estado: EstadoCancion): Int
	
	// ─────────────────────────────────────────────────────────────
	// Actualización de estados
	// ─────────────────────────────────────────────────────────────
	suspend fun actualizarEstado(id: Long, estado: EstadoCancion)
	
	suspend fun marcarComoLimpia(
		id: Long,
		titulo: String,
		artista: String,
		album: String,
		albumArtista: String?,
		genero: Genero?,
		anio: Int?,
		numeroPista: Int?,
	)
	
	suspend fun marcarComoEnriquecida(
		id: Long,
		geniusId: Long,
		geniusUrl: String,
		datosActualizados: Map<String, String>?,
	)
	
	// ─────────────────────────────────────────────────────────────
	// Escaneo y sincronización
	// ─────────────────────────────────────────────────────────────
	suspend fun insertarCruda(cancion: Cancion): Long
	
	suspend fun insertarCrudas(canciones: List<Cancion>): List<Long>
	
	suspend fun existePorHash(hash: String): Boolean
	
	suspend fun existePorRuta(ruta: String): Boolean
	
	suspend fun obtenerHashesExistentes(): Set<String>
	
	suspend fun eliminarPorRuta(ruta: String)
	
	suspend fun eliminarNoExistentes(rutasActuales: Set<String>): Int
	
	// ─────────────────────────────────────────────────────────────
	// Estadísticas de biblioteca
	// ─────────────────────────────────────────────────────────────
	suspend fun obtenerEstadisticas(): EstadisticasBiblioteca
}