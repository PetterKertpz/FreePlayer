package com.pmk.freeplayer.domain.useCase.playlist

import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.ListaReproduccion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

// ════════════════════════════════════════════════════════════
// CONSULTAS
// ════════════════════════════════════════════════════════════

class ObtenerTodasPlaylistsUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(): Flow<List<ListaReproduccion>> = repository.obtenerTodasLasPlaylists()
}

class ObtenerPlaylistPorIdUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(id: Long): Flow<ListaReproduccion?> = repository.obtenerPlaylistPorId(id)
}

class BuscarPlaylistsUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(consulta: String): Flow<List<ListaReproduccion>> {
		if (consulta.isBlank()) return emptyFlow()
		return repository.buscarPlaylists(consulta.trim())
	}
}

// ════════════════════════════════════════════════════════════
// CRUD DE PLAYLISTS
// ════════════════════════════════════════════════════════════

class CrearPlaylistUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(
		nombre: String,
		descripcion: String? = null
	): Result<Long> {
		return try {
			require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
			val id = repository.crearPlaylist(nombre.trim(), descripcion?.trim())
			Result.success(id)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class ActualizarPlaylistUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(
		id: Long,
		nombre: String,
		descripcion: String?
	): Result<Unit> {
		return try {
			require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
			repository.actualizarPlaylist(id, nombre.trim(), descripcion?.trim())
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class EliminarPlaylistUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(id: Long): Result<Unit> {
		return try {
			repository.eliminarPlaylist(id)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class DuplicarPlaylistUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(id: Long, nuevoNombre: String): Result<Long> {
		return try {
			require(nuevoNombre.isNotBlank()) { "El nuevo nombre no puede estar vacío" }
			val nuevoId = repository.duplicarPlaylist(id, nuevoNombre.trim())
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
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(playlistId: Long, cancionId: Long): Result<Unit> {
		return try {
			repository.agregarCancionAPlaylist(playlistId, cancionId)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class AgregarCancionesAPlaylistUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(playlistId: Long, cancionIds: List<Long>): Result<Unit> {
		return try {
			require(cancionIds.isNotEmpty()) { "La lista de canciones no puede estar vacía" }
			repository.agregarCancionesAPlaylist(playlistId, cancionIds)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class EliminarCancionDePlaylistUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(playlistId: Long, cancionId: Long): Result<Unit> {
		return try {
			repository.eliminarCancionDePlaylist(playlistId, cancionId)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class MoverCancionEnPlaylistUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(
		playlistId: Long,
		desdePosicion: Int,
		haciaPosicion: Int
	): Result<Unit> {
		return try {
			require(desdePosicion >= 0) { "desdePosicion debe ser >= 0" }
			require(haciaPosicion >= 0) { "haciaPosicion debe ser >= 0" }
			repository.moverCancionEnPlaylist(playlistId, desdePosicion, haciaPosicion)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class VerificarCancionEnPlaylistUseCase @Inject constructor(
	private val repository: BibliotecaRepository
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
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(id: Long, uri: String?): Result<Unit> {
		return try {
			repository.actualizarPortadaPlaylist(id, uri)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class GestionarColaReproduccionUseCase @Inject constructor(
	private val reproductorRepo: ReproductorRepository
) {
	
	/**
	 * "Play Next": Inserta las canciones justo después de la canción actual.
	 */
	suspend fun reproducirSiguiente(canciones: List<Song>): Result<Unit> = runCatching {
		require(canciones.isNotEmpty())
		
		val colaActual = reproductorRepo.obtenerColaActual().first()
		val listaMutable = colaActual.canciones.toMutableList()
		
		// Si la cola está vacía, simplemente reproducimos
		if (listaMutable.isEmpty()) {
			reproductorRepo.establecerCola(canciones, 0)
			return@runCatching
		}
		
		// Insertamos justo después del índice actual
		val indiceInsercion = colaActual.indiceActual + 1
		listaMutable.addAll(indiceInsercion, canciones)
		
		reproductorRepo.actualizarCola(listaMutable)
	}
	
	/**
	 * "Add to Queue": Agrega al final absoluto de la lista.
	 */
	suspend fun agregarAlFinal(canciones: List<Song>): Result<Unit> = runCatching {
		require(canciones.isNotEmpty())
		
		val colaActual = reproductorRepo.obtenerColaActual().first()
		val listaMutable = colaActual.canciones.toMutableList()
		
		listaMutable.addAll(canciones)
		
		// Si la cola estaba vacía, quizás queramos reproducir automáticamente (opcional)
		if (colaActual.canciones.isEmpty()) {
			reproductorRepo.establecerCola(listaMutable, 0)
		} else {
			reproductorRepo.actualizarCola(listaMutable)
		}
	}
	
	/**
	 * Reordenamiento (Drag & Drop en la UI)
	 */
	suspend fun moverElemento(desde: Int, hacia: Int): Result<Unit> = runCatching {
		val colaActual = reproductorRepo.obtenerColaActual().first()
		val listaMutable = colaActual.canciones.toMutableList()
		
		// Validaciones de rangos
		require(desde in listaMutable.indices && hacia in listaMutable.indices)
		
		val item = listaMutable.removeAt(desde)
		listaMutable.add(hacia, item)
		
		// IMPORTANTE: Calcular el nuevo índice de la canción actual si se movió
		var nuevoIndiceActual = colaActual.indiceActual
		if (desde == colaActual.indiceActual) {
			nuevoIndiceActual = hacia
		} else if (colaActual.indiceActual in (desde + 1)..hacia) {
			nuevoIndiceActual--
		} else if (colaActual.indiceActual in hacia..<desde) {
			nuevoIndiceActual++
		}
		
		reproductorRepo.actualizarCola(listaMutable, nuevoIndiceActual)
	}
	
	suspend fun eliminarElemento(posicion: Int): Result<Unit> = runCatching {
		// Lógica similar para eliminar y ajustar el índice actual si es necesario
		reproductorRepo.eliminarDeCola(posicion)
	}
}