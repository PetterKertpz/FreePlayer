package com.pmk.freeplayer.domain.usecase.playlist

import com.pmk.freeplayer.domain.model.ListaReproduccion
import com.pmk.freeplayer.domain.repository.ListaReproduccionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

// ════════════════════════════════════════════════════════════
// CONSULTAS
// ════════════════════════════════════════════════════════════

class ObtenerTodasPlaylistsUseCase @Inject constructor(
	private val repository: ListaReproduccionRepository
) {
	operator fun invoke(): Flow<List<ListaReproduccion>> = repository.obtenerTodas()
}

class ObtenerPlaylistPorIdUseCase @Inject constructor(
	private val repository: ListaReproduccionRepository
) {
	operator fun invoke(id: Long): Flow<ListaReproduccion?> = repository.obtenerPorId(id)
}

class BuscarPlaylistsUseCase @Inject constructor(
	private val repository: ListaReproduccionRepository
) {
	operator fun invoke(consulta: String): Flow<List<ListaReproduccion>> {
		if (consulta.isBlank()) return emptyFlow()
		return repository.buscar(consulta.trim())
	}
}

// ════════════════════════════════════════════════════════════
// CRUD DE PLAYLISTS
// ════════════════════════════════════════════════════════════

class CrearPlaylistUseCase @Inject constructor(
	private val repository: ListaReproduccionRepository
) {
	suspend operator fun invoke(
		nombre: String,
		descripcion: String? = null
	): Result<Long> {
		return try {
			require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
			val id = repository.crear(nombre.trim(), descripcion?.trim())
			Result.success(id)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class ActualizarPlaylistUseCase @Inject constructor(
	private val repository: ListaReproduccionRepository
) {
	suspend operator fun invoke(
		id: Long,
		nombre: String,
		descripcion: String?
	): Result<Unit> {
		return try {
			require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
			repository.actualizar(id, nombre.trim(), descripcion?.trim())
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class EliminarPlaylistUseCase @Inject constructor(
	private val repository: ListaReproduccionRepository
) {
	suspend operator fun invoke(id: Long): Result<Unit> {
		return try {
			repository.eliminar(id)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class DuplicarPlaylistUseCase @Inject constructor(
	private val repository: ListaReproduccionRepository
) {
	suspend operator fun invoke(id: Long, nuevoNombre: String): Result<Long> {
		return try {
			require(nuevoNombre.isNotBlank()) { "El nuevo nombre no puede estar vacío" }
			val nuevoId = repository.duplicar(id, nuevoNombre.trim())
			Result.success(nuevoId)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// GESTIÓN DE CANCIONES EN PLAYLIST
// ════════════════════════════════════════════════════════════

class AgregarCancionAPlaylistUseCase @Inject constructor(
	private val repository: ListaReproduccionRepository
) {
	suspend operator fun invoke(playlistId: Long, cancionId: Long): Result<Unit> {
		return try {
			repository.agregarCancion(playlistId, cancionId)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class AgregarCancionesAPlaylistUseCase @Inject constructor(
	private val repository: ListaReproduccionRepository
) {
	suspend operator fun invoke(playlistId: Long, cancionIds: List<Long>): Result<Unit> {
		return try {
			require(cancionIds.isNotEmpty()) { "La lista de canciones no puede estar vacía" }
			repository.agregarCanciones(playlistId, cancionIds)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class EliminarCancionDePlaylistUseCase @Inject constructor(
	private val repository: ListaReproduccionRepository
) {
	suspend operator fun invoke(playlistId: Long, cancionId: Long): Result<Unit> {
		return try {
			repository.eliminarCancion(playlistId, cancionId)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class MoverCancionEnPlaylistUseCase @Inject constructor(
	private val repository: ListaReproduccionRepository
) {
	suspend operator fun invoke(
		playlistId: Long,
		desdePosicion: Int,
		haciaPosicion: Int
	): Result<Unit> {
		return try {
			require(desdePosicion >= 0) { "desdePosicion debe ser >= 0" }
			require(haciaPosicion >= 0) { "haciaPosicion debe ser >= 0" }
			repository.moverCancion(playlistId, desdePosicion, haciaPosicion)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class VerificarCancionEnPlaylistUseCase @Inject constructor(
	private val repository: ListaReproduccionRepository
) {
	suspend operator fun invoke(playlistId: Long, cancionId: Long): Result<Boolean> {
		return try {
			Result.success(repository.existeCancionEnPlaylist(playlistId, cancionId))
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// PORTADA
// ════════════════════════════════════════════════════════════

class ActualizarPortadaPlaylistUseCase @Inject constructor(
	private val repository: ListaReproduccionRepository
) {
	suspend operator fun invoke(id: Long, uri: String?): Result<Unit> {
		return try {
			repository.actualizarPortada(id, uri)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}