package com.pmk.freeplayer.core.domain.model.state

sealed interface MediaProcessingState {
   data object Inactivo : MediaProcessingState

   // Estados con datos (Payload) para la UI
   data class Escaneando(val archivosProcesados: Int) : MediaProcessingState

   data class ExtrayendoMetadatos(val archivoActual: String) : MediaProcessingState

   data class Limpiando(val campo: String) : MediaProcessingState

   data class BuscandoEnGenius(val tituloCancion: String) : MediaProcessingState

   data class ScrapingLetra(val fuente: String) : MediaProcessingState

   data class Guardando(val itemsActualizados: Int) : MediaProcessingState

   // Manejo de errores robusto
   data class Error(val codigo: String, val mensaje: String, val excepcion: Throwable? = null) :
      MediaProcessingState

   data object Completado : MediaProcessingState
}