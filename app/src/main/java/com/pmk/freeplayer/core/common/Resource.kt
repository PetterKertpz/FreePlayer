package com.pmk.freeplayer.core.common

/**
 * Un contenedor genérico para manejar estados de operaciones de negocio.
 * Evita el uso de excepciones para el flujo de control.
 */
sealed class Resultado<out T> {
	
	data class Exito<out T>(val datos: T) : Resultado<T>()
	
	data class Error(
		val tipo: TipoError,
		val mensaje: String,
		val excepcion: Throwable? = null
	) : Resultado<Nothing>()
	
	data object Cargando : Resultado<Nothing>()
	
	// Helpers para verificar estado
	val esExito: Boolean get() = this is Exito
	val esError: Boolean get() = this is Error
	
	/**
	 * Permite transformar el dato de éxito si existe.
	 */
	inline fun <R> mapear(transformacion: (T) -> R): Resultado<R> {
		return when (this) {
			is Exito -> Exito(transformacion(datos))
			is Error -> this
			is Cargando -> Cargando
		}
	}
}

/**
 * Categorización de errores para que la UI sepa qué mostrar
 * (ej. mostrar un botón de "Reintentar" si es ERROR_RED).
 */
enum class TipoError {
	ARCHIVO_NO_ENCONTRADO,
	PERMISOS_DENEGADOS,
	ERROR_RED,
	DATOS_CORRUPTOS,
	RECURSO_NO_DISPONIBLE,
	DESCONOCIDO
}