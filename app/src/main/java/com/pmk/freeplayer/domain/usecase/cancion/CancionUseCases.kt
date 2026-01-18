package com.pmk.freeplayer.domain.usecase.cancion

import com.pmk.freeplayer.domain.model.Cancion
import com.pmk.freeplayer.domain.model.DatosLimpieza
import com.pmk.freeplayer.domain.model.EstadisticasBiblioteca
import com.pmk.freeplayer.domain.model.enums.EstadoCancion
import com.pmk.freeplayer.domain.model.enums.Genero
import com.pmk.freeplayer.domain.model.enums.TipoOrdenamiento
import com.pmk.freeplayer.domain.repository.CancionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

// ════════════════════════════════════════════════════════════
// CONSULTAS BÁSICAS
// ════════════════════════════════════════════════════════════

class ObtenerTodasCancionesUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	operator fun invoke(): Flow<List<Cancion>> = repository.obtenerTodas()
}

class ObtenerCancionPorIdUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	operator fun invoke(id: Long): Flow<Cancion?> = repository.obtenerPorId(id)
}

class ObtenerCancionesPorAlbumUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	operator fun invoke(albumId: Long): Flow<List<Cancion>> =
		repository.obtenerPorAlbum(albumId)
}

class ObtenerCancionesPorArtistaUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	operator fun invoke(artista: String): Flow<List<Cancion>> =
		repository.obtenerPorArtista(artista)
}

class ObtenerCancionesPorGeneroUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	operator fun invoke(genero: Genero): Flow<List<Cancion>> =
		repository.obtenerPorGenero(genero)
}

class ObtenerCancionesPorCarpetaUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	operator fun invoke(ruta: String): Flow<List<Cancion>> =
		repository.obtenerPorCarpeta(ruta)
}

class ObtenerCancionesOrdenadasUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	operator fun invoke(
		ordenamiento: TipoOrdenamiento = TipoOrdenamiento.TITULO_ASC
	): Flow<List<Cancion>> = repository.obtenerOrdenadas(ordenamiento)
}

class BuscarCancionesUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	operator fun invoke(consulta: String): Flow<List<Cancion>> {
		if (consulta.isBlank()) return emptyFlow()
		return repository.buscar(consulta.trim())
	}
}

// ════════════════════════════════════════════════════════════
// FAVORITOS
// ════════════════════════════════════════════════════════════

class ObtenerFavoritasUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	operator fun invoke(): Flow<List<Cancion>> = repository.obtenerFavoritas()
}

class MarcarComoFavoritaUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	suspend operator fun invoke(id: Long, esFavorita: Boolean): Result<Unit> {
		return try {
			repository.marcarComoFavorita(id, esFavorita)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class AlternarFavoritaUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	suspend operator fun invoke(id: Long): Result<Unit> {
		return try {
			repository.alternarFavorita(id)
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
	private val repository: CancionRepository
) {
	operator fun invoke(limite: Int = 50): Flow<List<Cancion>> =
		repository.obtenerMasReproducidas(limite)
}

class ObtenerReproducidasRecientementeUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	operator fun invoke(limite: Int = 50): Flow<List<Cancion>> =
		repository.obtenerReproducidasRecientemente(limite)
}

class ObtenerAgregadasRecientementeUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	operator fun invoke(limite: Int = 50): Flow<List<Cancion>> =
		repository.obtenerAgregadasRecientemente(limite)
}

class IncrementarReproduccionUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	suspend operator fun invoke(id: Long): Result<Unit> {
		return try {
			repository.incrementarReproduccion(id)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class ActualizarUltimaReproduccionUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	suspend operator fun invoke(
		id: Long,
		timestamp: Long = System.currentTimeMillis()
	): Result<Unit> {
		return try {
			repository.actualizarUltimaReproduccion(id, timestamp)
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
	private val repository: CancionRepository
) {
	operator fun invoke(estado: EstadoCancion): Flow<List<Cancion>> =
		repository.obtenerPorEstado(estado)
}

class ObtenerCancionesCrudasUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	operator fun invoke(): Flow<List<Cancion>> = repository.obtenerCrudas()
}

class ObtenerCancionesLimpiasUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	operator fun invoke(): Flow<List<Cancion>> = repository.obtenerLimpias()
}

class ObtenerCancionesEnriquecidasUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	operator fun invoke(): Flow<List<Cancion>> = repository.obtenerEnriquecidas()
}

class ContarPorEstadoUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	suspend operator fun invoke(estado: EstadoCancion): Result<Int> {
		return try {
			Result.success(repository.contarPorEstado(estado))
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// ACTUALIZACIÓN DE ESTADOS (Pipeline de procesamiento)
// ════════════════════════════════════════════════════════════

class MarcarComoLimpiaUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	suspend operator fun invoke(id: Long, datos: DatosLimpieza): Result<Unit> {
		return try {
			repository.marcarComoLimpia(
				id = id,
				titulo = datos.titulo,
				artista = datos.artista,
				album = datos.album,
				albumArtista = datos.albumArtista,
				genero = datos.genero,
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
	private val repository: CancionRepository
) {
	suspend operator fun invoke(
		id: Long,
		geniusId: Long,
		geniusUrl: String,
		datosActualizados: Map<String, String>? = null
	): Result<Unit> {
		return try {
			repository.marcarComoEnriquecida(id, geniusId, geniusUrl, datosActualizados)
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
	private val repository: CancionRepository
) {
	suspend operator fun invoke(cancion: Cancion): Result<Long> {
		return try {
			val id = repository.insertarCruda(cancion)
			Result.success(id)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class InsertarCancionesCrudasUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	suspend operator fun invoke(canciones: List<Cancion>): Result<List<Long>> {
		return try {
			val ids = repository.insertarCrudas(canciones)
			Result.success(ids)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class VerificarDuplicadoUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	suspend fun porHash(hash: String): Result<Boolean> {
		return try {
			Result.success(repository.existePorHash(hash))
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
	
	suspend fun porRuta(ruta: String): Result<Boolean> {
		return try {
			Result.success(repository.existePorRuta(ruta))
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class ObtenerHashesExistentesUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	suspend operator fun invoke(): Result<Set<String>> {
		return try {
			Result.success(repository.obtenerHashesExistentes())
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class EliminarCancionPorRutaUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	suspend operator fun invoke(ruta: String): Result<Unit> {
		return try {
			repository.eliminarPorRuta(ruta)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class EliminarNoExistentesUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	suspend operator fun invoke(rutasActuales: Set<String>): Result<Int> {
		return try {
			val eliminadas = repository.eliminarNoExistentes(rutasActuales)
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
	private val repository: CancionRepository
) {
	suspend operator fun invoke(): Result<Int> {
		return try {
			Result.success(repository.obtenerCantidadTotal())
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class ObtenerEstadisticasBibliotecaUseCase @Inject constructor(
	private val repository: CancionRepository
) {
	suspend operator fun invoke(): Result<EstadisticasBiblioteca> {
		return try {
			Result.success(repository.obtenerEstadisticas())
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}