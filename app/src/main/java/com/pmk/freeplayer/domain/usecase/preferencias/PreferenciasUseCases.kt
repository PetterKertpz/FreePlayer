package com.pmk.freeplayer.domain.usecase.preferencias

import com.pmk.freeplayer.domain.model.PreferenciasUsuario
import com.pmk.freeplayer.domain.model.enums.ColorAcento
import com.pmk.freeplayer.domain.model.enums.IdiomaApp
import com.pmk.freeplayer.domain.model.enums.ModoRepeticion
import com.pmk.freeplayer.domain.model.enums.ModoTema
import com.pmk.freeplayer.domain.model.enums.PresetEcualizador
import com.pmk.freeplayer.domain.model.enums.TamanioFuente
import com.pmk.freeplayer.domain.model.enums.TipoOrdenamiento
import com.pmk.freeplayer.domain.repository.PreferenciasRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ════════════════════════════════════════════════════════════
// OBTENER PREFERENCIAS COMPLETAS
// ════════════════════════════════════════════════════════════

class ObtenerPreferenciasUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	operator fun invoke(): Flow<PreferenciasUsuario> = repository.obtenerPreferencias()
}

// ════════════════════════════════════════════════════════════
// APARIENCIA
// ════════════════════════════════════════════════════════════

class SetModoTemaUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(modo: ModoTema): Result<Unit> {
		return try {
			repository.setModoTema(modo)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class SetColorAcentoUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(color: ColorAcento): Result<Unit> {
		return try {
			repository.setColorAcento(color)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class SetUsarColoresPortadaUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(usar: Boolean): Result<Unit> {
		return try {
			repository.setUsarColoresPortada(usar)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// REPRODUCTOR
// ════════════════════════════════════════════════════════════

class SetModoRepeticionPrefUseCase @Inject constructor(
	private val repository: PreferenciasRepository
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

class SetAleatorioPrefUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(activado: Boolean): Result<Unit> {
		return try {
			repository.setAleatorio(activado)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class SetReproduccionSinPausasUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(activado: Boolean): Result<Unit> {
		return try {
			repository.setReproduccionSinPausas(activado)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class SetDuracionFundidoUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(segundos: Int): Result<Unit> {
		return try {
			require(segundos in 0..12) { "Duración debe estar entre 0 y 12 segundos" }
			repository.setDuracionFundido(segundos)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class SetReanudarAlIniciarUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(activado: Boolean): Result<Unit> {
		return try {
			repository.setReanudarAlIniciar(activado)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// AUDIO
// ════════════════════════════════════════════════════════════

class SetEcualizadorActivadoUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(activado: Boolean): Result<Unit> {
		return try {
			repository.setEcualizadorActivado(activado)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class SetPresetEcualizadorUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(preset: PresetEcualizador): Result<Unit> {
		return try {
			repository.setPresetEcualizador(preset)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class SetNivelGravesUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(nivel: Int): Result<Unit> {
		return try {
			require(nivel in 0..100) { "Nivel debe estar entre 0 y 100" }
			repository.setNivelGraves(nivel)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class SetNivelVirtualizadorUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(nivel: Int): Result<Unit> {
		return try {
			require(nivel in 0..100) { "Nivel debe estar entre 0 y 100" }
			repository.setNivelVirtualizador(nivel)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class SetVelocidadReproduccionUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(velocidad: Float): Result<Unit> {
		return try {
			require(velocidad in 0.5f..2.0f) { "Velocidad debe estar entre 0.5x y 2.0x" }
			repository.setVelocidadReproduccion(velocidad)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class SetNormalizacionAudioUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(activado: Boolean): Result<Unit> {
		return try {
			repository.setNormalizacionAudio(activado)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// BIBLIOTECA
// ════════════════════════════════════════════════════════════

class SetOrdenamientoUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(ordenamiento: TipoOrdenamiento): Result<Unit> {
		return try {
			repository.setOrdenamiento(ordenamiento)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class SetColumnasGridUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(columnas: Int): Result<Unit> {
		return try {
			require(columnas in 1..5) { "Columnas debe estar entre 1 y 5" }
			repository.setColumnasGrid(columnas)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class SetMostrarCarpetasUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(mostrar: Boolean): Result<Unit> {
		return try {
			repository.setMostrarCarpetas(mostrar)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class SetCarpetasIgnoradasUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(carpetas: List<String>): Result<Unit> {
		return try {
			repository.setCarpetasIgnoradas(carpetas)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class SetDuracionMinimaUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(segundos: Int): Result<Unit> {
		return try {
			require(segundos >= 0) { "Duración mínima debe ser >= 0" }
			repository.setDuracionMinima(segundos)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// LETRAS
// ════════════════════════════════════════════════════════════

class SetBuscarLetrasAutoUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(activado: Boolean): Result<Unit> {
		return try {
			repository.setBuscarLetrasAuto(activado)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class SetMostrarTraduccionUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(mostrar: Boolean): Result<Unit> {
		return try {
			repository.setMostrarTraduccion(mostrar)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class SetTamanioFuenteLetrasUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(tamanio: TamanioFuente): Result<Unit> {
		return try {
			repository.setTamanioFuenteLetras(tamanio)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// NOTIFICACIONES
// ════════════════════════════════════════════════════════════

class SetMostrarNotificacionUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(mostrar: Boolean): Result<Unit> {
		return try {
			repository.setMostrarNotificacion(mostrar)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class SetControlesPantallaBloqueadaUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(mostrar: Boolean): Result<Unit> {
		return try {
			repository.setControlesPantallaBloqueada(mostrar)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// GENERAL
// ════════════════════════════════════════════════════════════

class SetIdiomaUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(idioma: IdiomaApp): Result<Unit> {
		return try {
			repository.setIdioma(idioma)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class SetTemporizadorSuenioPredeterminadoUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(minutos: Int): Result<Unit> {
		return try {
			require(minutos in 5..120) { "Minutos debe estar entre 5 y 120" }
			repository.setTemporizadorSuenioPredeterminado(minutos)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// RESET
// ════════════════════════════════════════════════════════════

class RestaurarPreferenciasUseCase @Inject constructor(
	private val repository: PreferenciasRepository
) {
	suspend operator fun invoke(): Result<Unit> {
		return try {
			repository.restaurarValoresPredeterminados()
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}