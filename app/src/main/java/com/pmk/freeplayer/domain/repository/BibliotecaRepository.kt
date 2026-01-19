package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Album
import com.pmk.freeplayer.domain.model.Artista
import com.pmk.freeplayer.domain.model.Cancion
import com.pmk.freeplayer.domain.model.Carpeta
import com.pmk.freeplayer.domain.model.EstadisticasBiblioteca
import com.pmk.freeplayer.domain.model.ListaReproduccion
import com.pmk.freeplayer.domain.model.audio.EstadoIntegridad
import com.pmk.freeplayer.domain.model.audio.Genero
import com.pmk.freeplayer.domain.model.config.Ordenamiento
import kotlinx.coroutines.flow.Flow

interface BibliotecaRepository {

   // ═════════════════════════════════════════════════════════════
   // CANCIONES
   // ═════════════════════════════════════════════════════════════

   // ─────────────────────────────────────────────────────────────
   // Obtener canciones
   // ─────────────────────────────────────────────────────────────
   fun obtenerTodasLasCanciones(): Flow<List<Cancion>>

   fun obtenerCancionPorId(id: Long): Flow<Cancion?>

   fun obtenerCancionesPorAlbum(albumId: Long): Flow<List<Cancion>>

   fun obtenerCancionesPorArtista(artista: String): Flow<List<Cancion>>

   fun obtenerCancionesPorGenero(genero: Genero): Flow<List<Cancion>>

   fun obtenerCancionesPorCarpeta(ruta: String): Flow<List<Cancion>>

   fun obtenerCancionesOrdenadas(ordenamiento: Ordenamiento): Flow<List<Cancion>>

   // ─────────────────────────────────────────────────────────────
   // Búsqueda de canciones
   // ─────────────────────────────────────────────────────────────
   fun buscarCanciones(consulta: String): Flow<List<Cancion>>

   // ─────────────────────────────────────────────────────────────
   // Favoritos
   // ─────────────────────────────────────────────────────────────
   fun obtenerCancionesFavoritas(): Flow<List<Cancion>>

   suspend fun marcarCancionComoFavorita(id: Long, esFavorita: Boolean)

   suspend fun alternarCancionFavorita(id: Long)

   // ─────────────────────────────────────────────────────────────
   // Estadísticas de reproducción
   // ─────────────────────────────────────────────────────────────
   fun obtenerCancionesMasReproducidas(limite: Int = 50): Flow<List<Cancion>>

   fun obtenerCancionesReproducidasRecientemente(limite: Int = 50): Flow<List<Cancion>>

   fun obtenerCancionesAgregadasRecientemente(limite: Int = 50): Flow<List<Cancion>>

   suspend fun incrementarReproduccionCancion(id: Long)

   suspend fun actualizarUltimaReproduccionCancion(id: Long, timestamp: Long)

   // ─────────────────────────────────────────────────────────────
   // Estadísticas generales de canciones
   // ─────────────────────────────────────────────────────────────
   suspend fun obtenerCantidadTotalCanciones(): Int

   // ─────────────────────────────────────────────────────────────
   // Gestión por estados
   // ─────────────────────────────────────────────────────────────
   fun obtenerCancionesPorEstado(estado: EstadoIntegridad): Flow<List<Cancion>>

   fun obtenerCancionesCrudas(): Flow<List<Cancion>>

   fun obtenerCancionesLimpias(): Flow<List<Cancion>>

   fun obtenerCancionesEnriquecidas(): Flow<List<Cancion>>

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
      genero: Genero?,
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
   suspend fun insertarCancionCruda(cancion: Cancion): Long

   suspend fun insertarCancionesCrudas(canciones: List<Cancion>): List<Long>

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

   fun obtenerTodosLosArtistas(): Flow<List<Artista>>

   fun obtenerArtistaPorId(id: Long): Flow<Artista?>

   fun obtenerArtistasOrdenados(ordenamiento: Ordenamiento): Flow<List<Artista>>

   fun buscarArtistas(consulta: String): Flow<List<Artista>>

   suspend fun obtenerCantidadTotalArtistas(): Int

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
   fun obtenerTodasLasPlaylists(): Flow<List<ListaReproduccion>>

   fun obtenerPlaylistPorId(id: Long): Flow<ListaReproduccion?>

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
   fun buscarPlaylists(consulta: String): Flow<List<ListaReproduccion>>
}
