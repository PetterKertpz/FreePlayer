package com.pmk.freeplayer.domain.usecase.reproductor

import com.pmk.freeplayer.domain.model.Cancion
import com.pmk.freeplayer.domain.model.ColaReproduccion
import com.pmk.freeplayer.domain.model.EstadoReproductorGuardado
import com.pmk.freeplayer.domain.model.enums.ModoRepeticion
import com.pmk.freeplayer.domain.repository.ReproductorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

// ════════════════════════════════════════════════════════════
// GUARDAR/RESTAURAR ESTADO
// ════════════════════════════════════════════════════════════

class GuardarEstadoReproductorUseCase @Inject constructor(
	private val repository: ReproductorRepository
) {
	suspend operator fun invoke(
		cancionId: Long?,
		posicion: Long,
		colaIds: List<Long>,
		indiceActual: Int
	): Result<Unit> {
		return try {
			repository.guardarEstado(cancionId, posicion, colaIds, indiceActual)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class ObtenerUltimaCancionIdUseCase @Inject constructor(
	private val repository: ReproductorRepository
) {
	operator fun invoke(): Flow<Long?> = repository.obtenerUltimaCancionId()
}

class ObtenerUltimaPosicionUseCase @Inject constructor(
	private val repository: ReproductorRepository
) {
	operator fun invoke(): Flow<Long> = repository.obtenerUltimaPosicion()
}

class ObtenerUltimaColaUseCase @Inject constructor(
	private val repository: ReproductorRepository
) {
	operator fun invoke(): Flow<ColaReproduccion> = repository.obtenerUltimaCola()
}

// ════════════════════════════════════════════════════════════
// CONFIGURACIÓN DE REPRODUCCIÓN
// ════════════════════════════════════════════════════════════

class ObtenerModoRepeticionUseCase @Inject constructor(
	private val repository: ReproductorRepository
) {
	operator fun invoke(): Flow<ModoRepeticion> = repository.obtenerModoRepeticion()
}

class SetModoRepeticionUseCase @Inject constructor(
	private val repository: ReproductorRepository
) {
	suspend operator fun invoke(modo: ModoRepeticion): Result<Unit> {
		return try {
			repository.setModoRepeticion(modo)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class ObtenerAleatorioActivadoUseCase @Inject constructor(
	private val repository: ReproductorRepository
) {
	operator fun invoke(): Flow<Boolean> = repository.obtenerAleatorioActivado()
}

class SetAleatorioActivadoUseCase @Inject constructor(
	private val repository: ReproductorRepository
) {
	suspend operator fun invoke(activado: Boolean): Result<Unit> {
		return try {
			repository.setAleatorioActivado(activado)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// GESTIÓN DE COLA
// ════════════════════════════════════════════════════════════

class EstablecerColaUseCase @Inject constructor(
	private val repository: ReproductorRepository
) {
	suspend operator fun invoke(
		canciones: List<Cancion>,
		indiceInicial: Int = 0
	): Result<Unit> {
		return try {
			require(canciones.isNotEmpty()) { "La lista de canciones no puede estar vacía" }
			require(indiceInicial in canciones.indices) { "Índice inicial fuera de rango" }
			repository.establecerCola(canciones, indiceInicial)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class AgregarAColaUseCase @Inject constructor(
	private val repository: ReproductorRepository
) {
	suspend operator fun invoke(cancion: Cancion): Result<Unit> {
		return try {
			repository.agregarACola(cancion)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
	
	suspend operator fun invoke(canciones: List<Cancion>): Result<Unit> {
		return try {
			require(canciones.isNotEmpty()) { "La lista no puede estar vacía" }
			repository.agregarACola(canciones)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class QuitarDeColaUseCase @Inject constructor(
	private val repository: ReproductorRepository
) {
	suspend operator fun invoke(indice: Int): Result<Unit> {
		return try {
			require(indice >= 0) { "El índice debe ser >= 0" }
			repository.quitarDeCola(indice)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class MoverEnColaUseCase @Inject constructor(
	private val repository: ReproductorRepository
) {
	suspend operator fun invoke(desde: Int, hasta: Int): Result<Unit> {
		return try {
			require(desde >= 0 && hasta >= 0) { "Los índices deben ser >= 0" }
			repository.moverEnCola(desde, hasta)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class LimpiarColaUseCase @Inject constructor(
	private val repository: ReproductorRepository
) {
	suspend operator fun invoke(): Result<Unit> {
		return try {
			repository.limpiarCola()
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// NAVEGACIÓN
// ════════════════════════════════════════════════════════════

class IrASiguienteUseCase @Inject constructor(
	private val repository: ReproductorRepository
) {
	suspend operator fun invoke(): Result<Cancion?> {
		return try {
			Result.success(repository.irASiguiente())
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class IrAAnteriorUseCase @Inject constructor(
	private val repository: ReproductorRepository
) {
	suspend operator fun invoke(): Result<Cancion?> {
		return try {
			Result.success(repository.irAAnterior())
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class IrAIndiceUseCase @Inject constructor(
	private val repository: ReproductorRepository
) {
	suspend operator fun invoke(indice: Int): Result<Cancion?> {
		return try {
			require(indice >= 0) { "El índice debe ser >= 0" }
			Result.success(repository.irAIndice(indice))
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// CASO DE USO COMPUESTO: Restaurar estado completo
// ════════════════════════════════════════════════════════════

class RestaurarEstadoReproductorUseCase @Inject constructor(
	private val repository: ReproductorRepository
) {
	operator fun invoke(): Flow<EstadoReproductorGuardado> {
		return combine(
			repository.obtenerUltimaCancionId(),
			repository.obtenerUltimaPosicion(),
			repository.obtenerUltimaCola(),
			repository.obtenerModoRepeticion(),
			repository.obtenerAleatorioActivado()
		) { cancionId, posicion, cola, modoRepeticion, aleatorio ->
			EstadoReproductorGuardado(
				cancionId = cancionId,
				posicion = posicion,
				cola = cola,
				modoRepeticion = modoRepeticion,
				aleatorioActivado = aleatorio
			)
		}
	}
	
	suspend fun obtenerUnaVez(): Result<EstadoReproductorGuardado> {
		return try {
			Result.success(invoke().first())
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}