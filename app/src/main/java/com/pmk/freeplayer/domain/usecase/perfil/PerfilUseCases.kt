package com.pmk.freeplayer.domain.usecase.perfil

import com.pmk.freeplayer.domain.model.PerfilUsuario
import com.pmk.freeplayer.domain.model.enums.Genero
import com.pmk.freeplayer.domain.repository.PerfilUsuarioRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ════════════════════════════════════════════════════════════
// CONSULTA
// ════════════════════════════════════════════════════════════

class ObtenerPerfilUseCase @Inject constructor(private val repository: PerfilUsuarioRepository) {
  operator fun invoke(): Flow<PerfilUsuario> = repository.obtenerPerfil()
}

// ════════════════════════════════════════════════════════════
// ACTUALIZACIÓN DE DATOS
// ════════════════════════════════════════════════════════════

class ActualizarNombreUseCase @Inject constructor(private val repository: PerfilUsuarioRepository) {
  suspend operator fun invoke(nombre: String): Result<Unit> {
    return try {
      require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
      require(nombre.length <= 50) { "El nombre no puede exceder 50 caracteres" }
      repository.actualizarNombre(nombre.trim())
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}

class ActualizarAvatarUseCase @Inject constructor(private val repository: PerfilUsuarioRepository) {
  suspend operator fun invoke(uri: String?): Result<Unit> {
    return try {
      repository.actualizarAvatar(uri)
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}

// ════════════════════════════════════════════════════════════
// ESTADÍSTICAS
// ════════════════════════════════════════════════════════════

class IncrementarTiempoEscuchadoUseCase
@Inject
constructor(private val repository: PerfilUsuarioRepository) {
  suspend operator fun invoke(milisegundos: Long): Result<Unit> {
    return try {
      require(milisegundos > 0) { "milisegundos debe ser > 0" }
      repository.incrementarTiempoEscuchado(milisegundos)
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}

class IncrementarCancionesReproducidasUseCase
@Inject
constructor(private val repository: PerfilUsuarioRepository) {
  suspend operator fun invoke(): Result<Unit> {
    return try {
      repository.incrementarCancionesReproducidas()
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}

class ActualizarGeneroFavoritoUseCase
@Inject
constructor(private val repository: PerfilUsuarioRepository) {
  suspend operator fun invoke(genero: Genero): Result<Unit> {
    return try {
      repository.actualizarGeneroFavorito(genero)
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}

class ActualizarArtistaFavoritoUseCase
@Inject
constructor(private val repository: PerfilUsuarioRepository) {
  suspend operator fun invoke(artista: String): Result<Unit> {
    return try {
      require(artista.isNotBlank()) { "El artista no puede estar vacío" }
      repository.actualizarArtistaFavorito(artista.trim())
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}

// ════════════════════════════════════════════════════════════
// RESET
// ════════════════════════════════════════════════════════════

class ReiniciarEstadisticasPerfilUseCase
@Inject
constructor(private val repository: PerfilUsuarioRepository) {
  suspend operator fun invoke(): Result<Unit> {
    return try {
      repository.reiniciarEstadisticas()
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
