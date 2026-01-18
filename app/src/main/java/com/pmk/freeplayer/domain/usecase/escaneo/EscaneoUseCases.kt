package com.pmk.freeplayer.domain.usecase.escaneo

import com.pmk.freeplayer.domain.model.EstadisticasEscaneo
import com.pmk.freeplayer.domain.model.ResultadoEnriquecimiento
import com.pmk.freeplayer.domain.model.ResultadoEscaneo
import com.pmk.freeplayer.domain.model.ResultadoLimpieza
import com.pmk.freeplayer.domain.repository.EscaneoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GuardarResultadoEnriquecimientoUseCase
@Inject
constructor(private val repository: EscaneoRepository) {
  suspend operator fun invoke(resultado: ResultadoEnriquecimiento): Result<Long> {
    return try {
      val id = repository.guardarResultadoEnriquecimiento(resultado)
      Result.success(id)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}

class GuardarResultadoEscaneoUseCase
@Inject
constructor(private val repository: EscaneoRepository) {
  suspend operator fun invoke(resultado: ResultadoEscaneo): Result<Long> {
    return try {
      val id = repository.guardarResultadoEscaneo(resultado)
      Result.success(id)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}

class GuardarResultadoLimpiezaUseCase
@Inject
constructor(private val repository: EscaneoRepository) {
  suspend operator fun invoke(resultado: ResultadoLimpieza): Result<Long> {
    return try {
      val id = repository.guardarResultadoLimpieza(resultado)
      Result.success(id)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}

class LimpiarHistorialEscaneosUseCase
@Inject
constructor(private val repository: EscaneoRepository) {
  suspend operator fun invoke(mantenerUltimos: Int = 10): Result<Unit> {
    return try {
      require(mantenerUltimos >= 0) { "mantenerUltimos debe ser >= 0" }
      repository.limpiarHistorialEscaneos(mantenerUltimos)
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}

class ObtenerHistorialEscaneosUseCase
@Inject
constructor(private val repository: EscaneoRepository) {
  operator fun invoke(limite: Int = 20): Flow<List<ResultadoEscaneo>> {
    return repository.obtenerHistorialEscaneos(limite)
  }
}

class ObtenerUltimoEscaneoUseCase @Inject constructor(private val repository: EscaneoRepository) {
  suspend operator fun invoke(): Result<ResultadoEscaneo?> {
    return try {
      Result.success(repository.obtenerUltimoEscaneo())
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}



class ObtenerEstadisticasEscaneoUseCase
@Inject
constructor(private val repository: EscaneoRepository) {
  suspend operator fun invoke(): Result<EstadisticasEscaneo> {
    return try {
      val estadisticas =
          EstadisticasEscaneo(
              totalEscaneos = repository.obtenerTotalEscaneos(),
              totalCancionesEscaneadas = repository.obtenerTotalCancionesEscaneadas(),
              totalCancionesLimpiadas = repository.obtenerTotalCancionesLimpiadas(),
              totalCancionesEnriquecidas = repository.obtenerTotalCancionesEnriquecidas(),
              totalLetrasObtenidas = repository.obtenerTotalLetrasObtenidas(),
              tiempoTotalProcesamiento = repository.obtenerTiempoTotalProcesamiento(),
          )
      Result.success(estadisticas)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
