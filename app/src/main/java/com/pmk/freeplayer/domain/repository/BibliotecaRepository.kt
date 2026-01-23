package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Album
import com.pmk.freeplayer.domain.model.Artist
import com.pmk.freeplayer.domain.model.Carpeta
import com.pmk.freeplayer.domain.model.EstadisticasBiblioteca
import com.pmk.freeplayer.domain.model.Playlist
import com.pmk.freeplayer.domain.model.SocialLink
import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.audio.EstadoIntegridad
import com.pmk.freeplayer.domain.model.audio.Genre
import com.pmk.freeplayer.domain.model.config.Ordenamiento
import kotlinx.coroutines.flow.Flow

interface BibliotecaRepository {

   // ═════════════════════════════════════════════════════════════
   // CANCIONES
   // ═════════════════════════════════════════════════════════════

   // ─────────────────────────────────────────────────────────────
   // Obtener canciones
   // ─────────────────────────────────────────────────────────────
   fun obtenerTodasLasCanciones(): Flow<List<Song>>

   fun obtenerCancionPorId(id: Long): Flow<Song?>

   fun obtenerCancionesPorAlbum(albumId: Long): Flow<List<Song>>

   fun obtenerCancionesPorArtista(artista: String): Flow<List<Song>>

   fun obtenerCancionesPorGenero(genre: Genre): Flow<List<Song>>

   fun obtenerCancionesPorCarpeta(ruta: String): Flow<List<Song>>

   fun obtenerCancionesOrdenadas(ordenamiento: Ordenamiento): Flow<List<Song>>

   // ─────────────────────────────────────────────────────────────
   // Búsqueda de canciones
   // ─────────────────────────────────────────────────────────────
   fun buscarCanciones(consulta: String): Flow<List<Song>>

   // ─────────────────────────────────────────────────────────────
   // Favoritos
   // ─────────────────────────────────────────────────────────────
   fun obtenerCancionesFavoritas(): Flow<List<Song>>

   suspend fun marcarCancionComoFavorita(id: Long, esFavorita: Boolean)

   suspend fun alternarCancionFavorita(id: Long)

   // ─────────────────────────────────────────────────────────────
   // Estadísticas de reproducción
   // ─────────────────────────────────────────────────────────────
   fun obtenerCancionesMasReproducidas(limite: Int = 50): Flow<List<Song>>

   fun obtenerCancionesReproducidasRecientemente(limite: Int = 50): Flow<List<Song>>

   fun obtenerCancionesAgregadasRecientemente(limite: Int = 50): Flow<List<Song>>

   suspend fun incrementarReproduccionCancion(id: Long)

   suspend fun actualizarUltimaReproduccionCancion(id: Long, timestamp: Long)

   // ─────────────────────────────────────────────────────────────
   // Estadísticas generales de canciones
   // ─────────────────────────────────────────────────────────────
   suspend fun obtenerCantidadTotalCanciones(): Int

   // ─────────────────────────────────────────────────────────────
   // Gestión por estados
   // ─────────────────────────────────────────────────────────────
   fun obtenerCancionesPorEstado(estado: EstadoIntegridad): Flow<List<Song>>

   fun obtenerCancionesCrudas(): Flow<List<Song>>

   fun obtenerCancionesLimpias(): Flow<List<Song>>

   fun obtenerCancionesEnriquecidas(): Flow<List<Song>>

   suspend fun contarCancionesPorEstado(estado: EstadoIntegridad): Int

   // ─────────────────────────────────────────────────────────────
   // Actualización de estados
   // ─────────────────────────────────────────────────────────────
   suspend fun actualizarEstadoIntegridad(id: Long, estado: EstadoIntegridad)

   suspend fun marcarCancionComoLimpia(
	   id: Long,
	   titulo: String,
	   artista: String,
	   album: String,
	   albumArtista: String?,
	   genre: Genre?,
	   anio: Int?,
	   numeroPista: Int?,
   )

   suspend fun marcarCancionComoEnriquecida(
      id: Long,
      geniusId: Long,
      geniusUrl: String,
      datosActualizados: Map<String, String>?,
   )

   // ─────────────────────────────────────────────────────────────
   // Escaneo y sincronización
   // ─────────────────────────────────────────────────────────────
   suspend fun insertarCancionCruda(song: Song): Long

   suspend fun insertarCancionesCrudas(canciones: List<Song>): List<Long>

   suspend fun existeCancionPorHash(hash: String): Boolean

   suspend fun existeCancionPorRuta(ruta: String): Boolean

   suspend fun obtenerHashesCancionesExistentes(): Set<String>

   suspend fun eliminarCancionPorRuta(ruta: String)

   suspend fun eliminarCancionesNoExistentes(rutasActuales: Set<String>): Int

   // ─────────────────────────────────────────────────────────────
   // Estadísticas de biblioteca
   // ─────────────────────────────────────────────────────────────
   suspend fun obtenerEstadisticasBiblioteca(): EstadisticasBiblioteca

   // ═════════════════════════════════════════════════════════════
   // ÁLBUMES
   // ═════════════════════════════════════════════════════════════

   fun obtenerTodosLosAlbumes(): Flow<List<Album>>

   fun obtenerAlbumPorId(id: Long): Flow<Album?>

   fun obtenerAlbumesPorArtista(artista: String): Flow<List<Album>>

   fun obtenerAlbumesOrdenados(ordenamiento: Ordenamiento): Flow<List<Album>>

   fun buscarAlbumes(consulta: String): Flow<List<Album>>

   suspend fun obtenerCantidadTotalAlbumes(): Int

   // ═════════════════════════════════════════════════════════════
   // ARTISTAS
   // ═════════════════════════════════════════════════════════════

   fun obtenerTodosLosArtistas(): Flow<List<Artist>>

   fun obtenerArtistaPorId(id: Long): Flow<Artist?>

   fun obtenerArtistasOrdenados(ordenamiento: Ordenamiento): Flow<List<Artist>>

   fun buscarArtistas(consulta: String): Flow<List<Artist>>

   suspend fun obtenerCantidadTotalArtistas(): Int
	
	// --- REDES SOCIALES ---
	
	// Agrega o Actualiza un link
	suspend fun saveSocialLink(artistId: Long, link: SocialLink)
	
	// Elimina un link específico
	suspend fun deleteSocialLink(linkId: Long)
	
	// Obtiene los links de un artista en tiempo real
	fun getSocialLinks(artistId: Long): Flow<List<SocialLink>>
	
   // ═════════════════════════════════════════════════════════════
   // CARPETAS
   // ═════════════════════════════════════════════════════════════

   fun obtenerTodasLasCarpetas(): Flow<List<Carpeta>>

   fun obtenerCarpetasVisibles(): Flow<List<Carpeta>>

   suspend fun ocultarCarpeta(ruta: String)

   suspend fun mostrarCarpeta(ruta: String)

   fun obtenerCarpetasOcultas(): Flow<List<Carpeta>>

   // ═════════════════════════════════════════════════════════════
   // LISTAS DE REPRODUCCIÓN
   // ═════════════════════════════════════════════════════════════

   // ─────────────────────────────────────────────────────────────
   // CRUD de playlists
   // ─────────────────────────────────────────────────────────────
   fun obtenerTodasLasPlaylists(): Flow<List<Playlist>>

   fun obtenerPlaylistPorId(id: Long): Flow<Playlist?>

   suspend fun crearPlaylist(nombre: String, descripcion: String? = null): Long

   suspend fun actualizarPlaylist(id: Long, nombre: String, descripcion: String?)

   suspend fun eliminarPlaylist(id: Long)

   suspend fun duplicarPlaylist(id: Long, nuevoNombre: String): Long

   // ─────────────────────────────────────────────────────────────
   // Gestión de canciones en playlist
   // ─────────────────────────────────────────────────────────────
   suspend fun agregarCancionAPlaylist(playlistId: Long, cancionId: Long)

   suspend fun agregarCancionesAPlaylist(playlistId: Long, cancionIds: List<Long>)

   suspend fun eliminarCancionDePlaylist(playlistId: Long, cancionId: Long)

   suspend fun moverCancionEnPlaylist(playlistId: Long, desdePosicion: Int, haciaPosicion: Int)

   suspend fun existeCancionEnPlaylist(playlistId: Long, cancionId: Long): Boolean

   // ─────────────────────────────────────────────────────────────
   // Portada de playlist
   // ─────────────────────────────────────────────────────────────
   suspend fun actualizarPortadaPlaylist(id: Long, uri: String?)

   // ─────────────────────────────────────────────────────────────
   // Búsqueda de playlists
   // ─────────────────────────────────────────────────────────────
   fun buscarPlaylists(consulta: String): Flow<List<Playlist>>
}
