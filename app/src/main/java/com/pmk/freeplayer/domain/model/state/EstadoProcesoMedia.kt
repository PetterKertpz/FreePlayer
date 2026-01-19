package com.pmk.freeplayer.domain.model.state

sealed interface EstadoProcesoMedia {
   data object Inactivo : EstadoProcesoMedia

   // Estados con datos (Payload) para la UI
   data class Escaneando(val archivosProcesados: Int) : EstadoProcesoMedia

   data class ExtrayendoMetadatos(val archivoActual: String) : EstadoProcesoMedia

   data class Limpiando(val campo: String) : EstadoProcesoMedia

   data class BuscandoEnGenius(val tituloCancion: String) : EstadoProcesoMedia

   data class ScrapingLetra(val fuente: String) : EstadoProcesoMedia

   data class Guardando(val itemsActualizados: Int) : EstadoProcesoMedia

   // Manejo de errores robusto
   data class Error(val codigo: String, val mensaje: String, val excepcion: Throwable? = null) :
      EstadoProcesoMedia

   data object Completado : EstadoProcesoMedia
}
