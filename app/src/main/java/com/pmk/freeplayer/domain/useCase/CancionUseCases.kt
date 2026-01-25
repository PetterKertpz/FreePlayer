package com.pmk.freeplayer.domain.useCase

import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.DatosLimpieza
import com.pmk.freeplayer.domain.model.LibraryStats
import com.pmk.freeplayer.domain.model.Genre
import com.pmk.freeplayer.domain.model.enums.IntegrityStatus
import com.pmk.freeplayer.domain.model.config.CriterioOrdenamiento
import com.pmk.freeplayer.domain.model.config.DireccionOrdenamiento
import com.pmk.freeplayer.domain.model.config.Ordenamiento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

// ════════════════════════════════════════════════════════════
// CONSULTAS BÁSICAS
// ════════════════════════════════════════════════════════════

class ObtenerTodasCancionesUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(): Flow<List<Song>> = repository.obtenerTodasLasCanciones()
}

class ObtenerCancionPorIdUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(id: Long): Flow<Song?> = repository.obtenerCancionPorId(id)
}

class ObtenerCancionesPorAlbumUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(albumId: Long): Flow<List<Song>> =
		repository.obtenerCancionesPorAlbum(albumId)
}

class ObtenerCancionesPorArtistaUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(artista: String): Flow<List<Song>> =
		repository.obtenerCancionesPorArtista(artista)
}

class ObtenerCancionesPorGeneroUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(genre: Genre): Flow<List<Song>> =
		repository.obtenerCancionesPorGenero(genre)
}

class ObtenerCancionesPorCarpetaUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(ruta: String): Flow<List<Song>> =
		repository.obtenerCancionesPorCarpeta(ruta)
}

class ObtenerCancionesOrdenadasUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(
		ordenamiento: Ordenamiento = Ordenamiento(CriterioOrdenamiento.TITULO, DireccionOrdenamiento.ASCENDENTE)
	): Flow<List<Song>> = repository.obtenerCancionesOrdenadas(ordenamiento)
}

class BuscarCancionesUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(consulta: String): Flow<List<Song>> {
		if (consulta.isBlank()) return emptyFlow()
		return repository.buscarCanciones(consulta.trim())
	}
}

// ════════════════════════════════════════════════════════════
// FAVORITOS
// ════════════════════════════════════════════════════════════

class ObtenerFavoritasUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(): Flow<List<Song>> = repository.obtenerCancionesFavoritas()
}

class MarcarComoFavoritaUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(id: Long, esFavorita: Boolean): Result<Unit> {
		return try {
			repository.marcarCancionComoFavorita(id, esFavorita)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class AlternarFavoritaUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(id: Long): Result<Unit> {
		return try {
			repository.alternarCancionFavorita(id)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// ESTADÍSTICAS DE REPRODUCCIÓN
// ════════════════════════════════════════════════════════════

class ObtenerMasReproducidasUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(limite: Int = 50): Flow<List<Song>> =
		repository.obtenerCancionesMasReproducidas(limite)
}

class ObtenerReproducidasRecientementeUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(limite: Int = 50): Flow<List<Song>> =
		repository.obtenerCancionesReproducidasRecientemente(limite)
}

class ObtenerAgregadasRecientementeUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(limite: Int = 50): Flow<List<Song>> =
		repository.obtenerCancionesAgregadasRecientemente(limite)
}

class IncrementarReproduccionUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(id: Long): Result<Unit> {
		return try {
			repository.incrementarReproduccionCancion(id)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class ActualizarUltimaReproduccionUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(
		id: Long,
		timestamp: Long = System.currentTimeMillis()
	): Result<Unit> {
		return try {
			repository.actualizarUltimaReproduccionCancion(id, timestamp)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// GESTIÓN POR ESTADOS
// ════════════════════════════════════════════════════════════

class ObtenerCancionesPorEstadoUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(estado: IntegrityStatus): Flow<List<Song>> =
		repository.obtenerCancionesPorEstado(estado)
}

class ObtenerCancionesCrudasUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(): Flow<List<Song>> = repository.obtenerCancionesCrudas()
}

class ObtenerCancionesLimpiasUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(): Flow<List<Song>> = repository.obtenerCancionesLimpias()
}

class ObtenerCancionesEnriquecidasUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(): Flow<List<Song>> = repository.obtenerCancionesEnriquecidas()
}

class ContarPorEstadoUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(estado: IntegrityStatus): Result<Int> {
		return try {
			Result.success(repository.contarCancionesPorEstado(estado))
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// ACTUALIZACIÓN DE ESTADOS (Pipeline de procesamiento)
// ════════════════════════════════════════════════════════════

class MarcarComoLimpiaUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(id: Long, datos: DatosLimpieza): Result<Unit> {
		return try {
			repository.marcarCancionComoLimpia(
				id = id,
				titulo = datos.titulo,
				artista = datos.artista,
				album = datos.album,
				albumArtista = datos.albumArtista,
				genre = datos.genre,
				anio = datos.anio,
				numeroPista = datos.numeroPista
			)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class MarcarComoEnriquecidaUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(
		id: Long,
		geniusId: Long,
		geniusUrl: String,
		datosActualizados: Map<String, String>? = null
	): Result<Unit> {
		return try {
			repository.marcarCancionComoEnriquecida(id, geniusId, geniusUrl, datosActualizados)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// INSERCIÓN Y SINCRONIZACIÓN
// ════════════════════════════════════════════════════════════

class InsertarCancionCrudaUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(song: Song): Result<Long> {
		return try {
			val id = repository.insertarCancionCruda(song)
			Result.success(id)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class InsertarCancionesCrudasUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(canciones: List<Song>): Result<List<Long>> {
		return try {
			val ids = repository.insertarCancionesCrudas(canciones)
			Result.success(ids)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class VerificarDuplicadoUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend fun porHash(hash: String): Result<Boolean> {
		return try {
			Result.success(repository.existeCancionPorHash(hash))
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
	
	suspend fun porRuta(ruta: String): Result<Boolean> {
		return try {
			Result.success(repository.existeCancionPorRuta(ruta))
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class ObtenerHashesExistentesUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(): Result<Set<String>> {
		return try {
			Result.success(repository.obtenerHashesCancionesExistentes())
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class EliminarCancionPorRutaUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(ruta: String): Result<Unit> {
		return try {
			repository.eliminarCancionPorRuta(ruta)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class EliminarNoExistentesUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(rutasActuales: Set<String>): Result<Int> {
		return try {
			val eliminadas = repository.eliminarCancionesNoExistentes(rutasActuales)
			Result.success(eliminadas)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// ESTADÍSTICAS DE BIBLIOTECA
// ════════════════════════════════════════════════════════════

class ObtenerCantidadTotalUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(): Result<Int> {
		return try {
			Result.success(repository.obtenerCantidadTotalCanciones())
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class ObtenerEstadisticasBibliotecaUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(): Result<LibraryStats> {
		return try {
			Result.success(repository.obtenerEstadisticasBiblioteca())
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}