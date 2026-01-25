package com.pmk.freeplayer.domain.useCase

import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.Queue
import com.pmk.freeplayer.domain.model.Duracion
import com.pmk.freeplayer.domain.model.Genre
import com.pmk.freeplayer.domain.model.MediaProcessingState
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class ReproducirCancionUseCase
@Inject
constructor(
   private val reproductorRepo: ReproductorRepository,
   private val bibliotecaRepo: BibliotecaRepository,
   private val usuarioRepo: UsuarioRepository,
   private val validadorArchivos: ValidadorArchivosAudio,
) {
   suspend operator fun invoke(
	   song: Song,
	   agregarACola: Boolean = true,
	   posicionInicial: Duracion = Duracion.CERO,
   ): Result<Song> {
      return try {
         // 1. Validar archivo
         val validacion = validadorArchivos.validar(song.ruta)
         if (!validacion.esValido) {
            return Result.failure(
               ErrorArchivoNoValido("Archivo no válido: ${validacion.razon}", song)
            )
         }

         // 2. Verificar canción en biblioteca
         val cancionActualizada =
            bibliotecaRepo.obtenerCancionPorId(song.id).first()
               ?: return Result.failure(
                  ErrorCancionNoEncontrada("Canción con ID ${song.id} no existe")
               )

         // 3. Establecer en cola si es necesario
         if (agregarACola) {
            val colaActual = reproductorRepo.obtenerColaActual().first()
            if (colaActual.estaVacia || colaActual.songActual?.id != song.id) {
               reproductorRepo.establecerCola(
                  canciones = listOf(cancionActualizada),
                  indiceInicial = 0,
               )
            }
         }

         // 4. Actualizar estadísticas
         val timestamp = System.currentTimeMillis()
         bibliotecaRepo.incrementarReproduccionCancion(song.id)
         bibliotecaRepo.actualizarUltimaReproduccionCancion(song.id, timestamp)
         usuarioRepo.incrementarCancionesReproducidas()

         // 5. Guardar estado de sesión
         val colaActual = reproductorRepo.obtenerColaActual().first()
         reproductorRepo.guardarEstadoSesion(
            cancionId = song.id,
            posicionMs = posicionInicial.milisegundos,
            colaIds = colaActual.canciones.map { it.id },
            indiceActual = colaActual.indiceActual,
         )

         // 6. Log
         usuarioRepo.logInfo(
            fase = MediaProcessingState.Inactivo,
            mensaje = "Reproduciendo: ${song.titulo} - ${song.artista}",
            cancionId = song.id,
            detalles =
               mapOf(
                  "duracion" to "${song.duracion.enSegundos}s",
                  "formato" to song.tipoMime,
                  "bitrate" to "${song.tasaBits / 1000}kbps",
               ),
         )

         Result.success(cancionActualizada)
      } catch (e: Exception) {
         usuarioRepo.logError(
            fase =
               MediaProcessingState.Error(
                  codigo = "PLAY_ERROR",
                  mensaje = "Error al reproducir canción",
               ),
            mensaje = "Fallo al reproducir '${song.titulo}': ${e.message}",
            cancionId = song.id,
            excepcion = e,
         )
         Result.failure(e)
      }
   }
}

/** Reproduce una lista completa (álbum, playlist, etc.) con opciones de aleatorio */
class ReproducirListaUseCase
@Inject
constructor(
   private val reproductorRepo: ReproductorRepository,
   private val bibliotecaRepo: BibliotecaRepository,
   private val usuarioRepo: UsuarioRepository,
   private val validadorArchivos: ValidadorArchivosAudio,
) {
   suspend operator fun invoke(
	   canciones: List<Song>,
	   indiceInicial: Int = 0,
	   mezclar: Boolean = false,
   ): Result<Queue> {
      return try {
         require(canciones.isNotEmpty()) { "La lista de canciones está vacía" }
         require(indiceInicial in canciones.indices) { "Índice inicial inválido" }

         // Filtrar canciones válidas
         val cancionesValidas =
            canciones.filter { cancion -> validadorArchivos.validar(cancion.ruta).esValido }

         if (cancionesValidas.isEmpty()) {
            return Result.failure(
               ErrorNoHayCancionesValidas("Ninguna canción en la lista es válida")
            )
         }

         // Ajustar índice si se filtraron canciones
         val nuevoIndice = if (indiceInicial >= cancionesValidas.size) 0 else indiceInicial

         // Mezclar si es necesario
         val listaFinal =
            if (mezclar) {
               val primera = cancionesValidas[nuevoIndice]
               val resto =
                  cancionesValidas.filterIndexed { idx, _ -> idx != nuevoIndice }.shuffled()
               listOf(primera) + resto
            } else {
               cancionesValidas
            }

         // Establecer cola
         reproductorRepo.establecerCola(
            canciones = listaFinal,
            indiceInicial = if (mezclar) 0 else nuevoIndice,
         )

         val cola =
            Queue(canciones = listaFinal, indiceActual = if (mezclar) 0 else nuevoIndice)

         // Actualizar estadísticas de la primera canción
         val primeraCancion = listaFinal.first()
         bibliotecaRepo.incrementarReproduccionCancion(primeraCancion.id)
         bibliotecaRepo.actualizarUltimaReproduccionCancion(
            primeraCancion.id,
            System.currentTimeMillis(),
         )

         // Log
         val duracionTotal =
            cancionesValidas.map { it.duracion }.fold(Duracion.CERO) { acc, dur -> acc + dur }

         usuarioRepo.logInfo(
            fase = MediaProcessingState.Inactivo,
            mensaje =
               "Reproduciendo lista: ${cola.cantidadTotal} canciones, ${duracionTotal.enMinutos} min",
            detalles =
               mapOf(
                  "mezclado" to mezclar.toString(),
                  "cancionesInvalidas" to "${canciones.size - cancionesValidas.size}",
               ),
         )

         Result.success(cola)
      } catch (e: Exception) {
         usuarioRepo.logError(
            fase =
               MediaProcessingState.Error(
                  codigo = "PLAY_LIST_ERROR",
                  mensaje = "Error al reproducir lista",
               ),
            mensaje = e.message ?: "Error desconocido",
            excepcion = e,
         )
         Result.failure(e)
      }
   }
}

// ════════════════════════════════════════════════════════════
// 📊 ESTADÍSTICAS Y SEGUIMIENTO
// ════════════════════════════════════════════════════════════

/** Registra el progreso de reproducción para estadísticas precisas */
class RegistrarProgresoReproduccionUseCase
@Inject
constructor(
   private val usuarioRepo: UsuarioRepository,
   private val bibliotecaRepo: BibliotecaRepository,
) {
   suspend operator fun invoke(
      cancionId: Long,
      duracionEscuchada: Duracion,
      porcentajeEscuchado: Float,
      completada: Boolean = false,
   ): Result<Unit> {
      return try {
         require(porcentajeEscuchado in 0f..1f) { "Porcentaje debe estar entre 0 y 1" }

         // Solo registrar si se escuchó una porción significativa (>30%)
         if (porcentajeEscuchado >= 0.3f) {
            usuarioRepo.registrarReproduccion(
               cancionId = cancionId,
               duracionEscuchada = duracionEscuchada.milisegundos,
               completada = completada,
            )

            usuarioRepo.incrementarTiempoEscuchado(duracionEscuchada.milisegundos)
         }

         Result.success(Unit)
      } catch (e: Exception) {
         Result.failure(e)
      }
   }
}

/** Obtiene recomendaciones basadas en el historial de reproducción */
class ObtenerRecomendacionesUseCase
@Inject
constructor(
   private val bibliotecaRepo: BibliotecaRepository,
   private val usuarioRepo: UsuarioRepository,
) {
   suspend operator fun invoke(
      cantidad: Int = 20,
      excluirRecientes: Boolean = true,
   ): Result<List<Song>> {
      return try {
         require(cantidad > 0) { "La cantidad debe ser > 0" }

         // Obtener historial reciente
         val historial = usuarioRepo.obtenerHistorial(limite = 100).first()

         // Extraer géneros y artistas más escuchados
         val generosPreferidos =
            historial
               .map { it.song.genre }
               .groupingBy { it }
               .eachCount()
               .entries
               .sortedByDescending { it.value }
               .take(3)
               .map { it.key }

         val artistasPreferidos =
            historial
               .map { it.song.artista }
               .groupingBy { it }
               .eachCount()
               .entries
               .sortedByDescending { it.value }
               .take(5)
               .map { it.key }

         // Obtener canciones candidatas
         val candidatas = mutableListOf<Song>()

         // Por género
         generosPreferidos.forEach { genero ->
            val porGenero = bibliotecaRepo.obtenerCancionesPorGenero(genero).first()
            candidatas.addAll(porGenero.take(10))
         }

         // Por artista
         artistasPreferidos.forEach { artista ->
            val porArtista = bibliotecaRepo.obtenerCancionesPorArtista(artista).first()
            candidatas.addAll(porArtista.take(10))
         }

         // Filtrar duplicados y recientes
         val cancionesRecientes =
            if (excluirRecientes) {
               historial.map { it.song.id }.toSet()
            } else {
               emptySet()
            }

         val recomendaciones =
            candidatas
               .distinctBy { it.id }
               .filterNot { it.id in cancionesRecientes }
               .shuffled()
               .take(cantidad)

         usuarioRepo.logInfo(
            fase = MediaProcessingState.Inactivo,
            mensaje = "Generadas ${recomendaciones.size} recomendaciones basadas en historial",
         )

         Result.success(recomendaciones)
      } catch (e: Exception) {
         Result.failure(e)
      }
   }
}

// ════════════════════════════════════════════════════════════
// 🔊 GESTIÓN DE AUDIO
// ════════════════════════════════════════════════════════════

/** Valida calidad de enums y sugiere mejoras */
class AnalizarCalidadAudioUseCase
@Inject
constructor(private val bibliotecaRepo: BibliotecaRepository) {
   suspend operator fun invoke(cancionId: Long): Result<AnalisisCalidadAudio> {
      return try {
         val cancion =
            bibliotecaRepo.obtenerCancionPorId(cancionId).first()
               ?: return Result.failure(ErrorCancionNoEncontrada("Canción no encontrada"))

         val calidad =
            when {
               cancion.tasaBits >= 320_000 -> CalidadAudio.EXCELENTE
               cancion.tasaBits >= 256_000 -> CalidadAudio.MUY_BUENA
               cancion.tasaBits >= 192_000 -> CalidadAudio.BUENA
               cancion.tasaBits >= 128_000 -> CalidadAudio.ACEPTABLE
               else -> CalidadAudio.BAJA
            }

         val sugerencias = mutableListOf<String>()

         if (calidad == CalidadAudio.BAJA) {
            sugerencias.add("Considera buscar una versión de mayor calidad")
         }

         if (cancion.tasaMuestreo < 44100) {
            sugerencias.add("Tasa de muestreo baja (${cancion.tasaMuestreo} Hz)")
         }

         val analisis =
            AnalisisCalidadAudio(
               cancionId = cancionId,
               calidad = calidad,
               tasaBits = cancion.tasaBits,
               tasaMuestreo = cancion.tasaMuestreo,
               formato = cancion.tipoMime,
               sugerencias = sugerencias,
            )

         Result.success(analisis)
      } catch (e: Exception) {
         Result.failure(e)
      }
   }
}

// ════════════════════════════════════════════════════════════
// 🎯 CASOS DE USO COMPUESTOS
// ════════════════════════════════════════════════════════════

/** Crea una cola de reproducción inteligente basada en criterios múltiples */
class CrearColaInteligente
@Inject
constructor(
   private val bibliotecaRepo: BibliotecaRepository,
   private val usuarioRepo: UsuarioRepository,
   private val reproductorRepo: ReproductorRepository,
) {
   suspend operator fun invoke(criterios: CriteriosColaInteligente): Result<Queue> {
      return try {
         val candidatas = mutableListOf<Song>()

         // 1. Aplicar filtros
         if (criterios.genres.isNotEmpty()) {
            criterios.genres.forEach { genero ->
               candidatas.addAll(bibliotecaRepo.obtenerCancionesPorGenero(genero).first())
            }
         } else {
            candidatas.addAll(bibliotecaRepo.obtenerTodasLasCanciones().first())
         }

         // 2. Filtrar por calidad mínima si se especifica
         criterios.calidadMinima?.let { calidadMin ->
            candidatas.retainAll { cancion -> cancion.tasaBits >= calidadMin }
         }

         // 3. Filtrar por duración
         criterios.duracionMinima?.let { durMin -> candidatas.retainAll { it.duracion >= durMin } }
         criterios.duracionMaxima?.let { durMax -> candidatas.retainAll { it.duracion <= durMax } }

         // 4. Aplicar límite de cantidad
         val cancionesFinales =
            candidatas.distinctBy { it.id }.shuffled().take(criterios.cantidadMaxima)

         if (cancionesFinales.isEmpty()) {
            return Result.failure(
               ErrorNoHayCancionesValidas("No hay canciones que cumplan los criterios")
            )
         }

         // 5. Crear cola
         reproductorRepo.establecerCola(canciones = cancionesFinales, indiceInicial = 0)

         val cola = Queue(canciones = cancionesFinales, indiceActual = 0)

         usuarioRepo.logInfo(
            fase = MediaProcessingState.Inactivo,
            mensaje = "Cola inteligente creada: ${cola.cantidadTotal} canciones",
            detalles =
               mapOf(
                  "genres" to criterios.genres.joinToString(),
                  "filtradas" to "${candidatas.size - cancionesFinales.size}",
               ),
         )

         Result.success(cola)
      } catch (e: Exception) {
         Result.failure(e)
      }
   }
}

// ════════════════════════════════════════════════════════════
// 📦 MODELOS DE DATOS
// ════════════════════════════════════════════════════════════

data class ResultadoValidacion(val esValido: Boolean, val razon: String? = null)

data class AnalisisCalidadAudio(
   val cancionId: Long,
   val calidad: CalidadAudio,
   val tasaBits: Int,
   val tasaMuestreo: Int,
   val formato: String,
   val sugerencias: List<String>,
)

enum class CalidadAudio {
   BAJA,
   ACEPTABLE,
   BUENA,
   MUY_BUENA,
   EXCELENTE,
}

data class CriteriosColaInteligente(
	val genres: List<Genre> = emptyList(),
	val artistas: List<String> = emptyList(),
	val calidadMinima: Int? = null, // bitrate mínimo
	val duracionMinima: Duracion? = null,
	val duracionMaxima: Duracion? = null,
	val cantidadMaxima: Int = 50,
	val excluirRecientes: Boolean = true,
)

// ════════════════════════════════════════════════════════════
// 🛠️ UTILIDADES
// ════════════════════════════════════════════════════════════

/** Validador de archivos de enums - debe implementarse en capa de infraestructura */
interface ValidadorArchivosAudio {
   suspend fun validar(ruta: String): ResultadoValidacion

   suspend fun existeArchivo(ruta: String): Boolean

   suspend fun obtenerFormatoReal(ruta: String): String?
}

// ════════════════════════════════════════════════════════════
// ⚠️ ERRORES ESPECÍFICOS
// ════════════════════════════════════════════════════════════

class ErrorArchivoNoValido(message: String, val song: Song) : Exception(message)

class ErrorCancionNoEncontrada(message: String) : Exception(message)

class ErrorNoHayCancionesValidas(message: String) : Exception(message)
