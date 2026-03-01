package com.pmk.freeplayer.core.common.exception

/**
 * 🚨 DOMAIN EXCEPTION HIERARCHY
 *
 * Excepciones tipadas del dominio que encapsulan errores de las capas inferiores.
 * Esto permite que la capa de presentación maneje errores de forma agnóstica
 * a la implementación (Room, Retrofit, etc.)
 *
 * Principio: La capa de dominio NO debe conocer detalles de implementación.
 */
sealed class DomainException(
	override val message: String,
	override val cause: Throwable? = null
) : Exception(message, cause) {
	
	// ═══════════════════════════════════════════════════════════════
	// DATA SOURCE ERRORS
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * Error relacionado con la base de datos local (Room/SQLite)
	 */
	data class DatabaseError(
		override val message: String,
		override val cause: Throwable? = null
	) : DomainException(message, cause)
	
	/**
	 * Error de red al comunicarse con APIs remotas
	 */
	data class NetworkError(
		override val message: String,
		val httpCode: Int? = null,
		override val cause: Throwable? = null
	) : DomainException(message, cause) {
		
		val isConnectionError: Boolean
			get() = httpCode == null
		
		val isServerError: Boolean
			get() = httpCode != null && httpCode in 500..599
		
		val isClientError: Boolean
			get() = httpCode != null && httpCode in 400..499
	}
	
	// ═══════════════════════════════════════════════════════════════
	// BUSINESS LOGIC ERRORS
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * Recurso no encontrado (artista, canción, álbum, etc.)
	 */
	data class NotFoundError(
		override val message: String,
		val resourceId: Any? = null,
		override val cause: Throwable? = null
	) : DomainException(message, cause)
	
	/**
	 * Datos inválidos o que no pasan validación
	 */
	data class ValidationError(
		override val message: String,
		val fieldErrors: Map<String, String> = emptyMap(),
		override val cause: Throwable? = null
	) : DomainException(message, cause)
	
	/**
	 * Estado inválido de la aplicación
	 */
	data class InvalidStateError(
		override val message: String,
		override val cause: Throwable? = null
	) : DomainException(message, cause)
	
	// ═══════════════════════════════════════════════════════════════
	// FILE SYSTEM ERRORS
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * Error al acceder o manipular archivos de audio
	 */
	data class FileAccessError(
		override val message: String,
		val filePath: String? = null,
		override val cause: Throwable? = null
	) : DomainException(message, cause)
	
	/**
	 * Archivo de audio corrupto o formato no soportado
	 */
	data class UnsupportedFormatError(
		override val message: String,
		val format: String? = null,
		override val cause: Throwable? = null
	) : DomainException(message, cause)
	
	// ═══════════════════════════════════════════════════════════════
	// PERMISSION ERRORS
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * Falta permiso del sistema (storage, etc.)
	 */
	data class PermissionDeniedError(
		override val message: String,
		val permission: String? = null,
		override val cause: Throwable? = null
	) : DomainException(message, cause)
	
	// ═══════════════════════════════════════════════════════════════
	// GENERIC FALLBACK
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * Error desconocido o no categorizado
	 * Usar solo cuando no se pueda determinar el tipo específico
	 */
	data class UnknownError(
		override val message: String,
		override val cause: Throwable? = null
	) : DomainException(message, cause)
}

// ═══════════════════════════════════════════════════════════════
// EXTENSION FUNCTIONS - Para facilitar el manejo en ViewModels
// ═══════════════════════════════════════════════════════════════

/**
 * Obtiene un mensaje amigable para el usuario
 */
fun DomainException.toUserFriendlyMessage(): String {
	return when (this) {
		is DomainException.DatabaseError -> "Error al acceder a los datos locales"
		is DomainException.NetworkError -> when {
			isConnectionError -> "Sin conexión a internet"
			isServerError -> "El servidor no está disponible"
			isClientError -> "Error en la solicitud"
			else -> "Error de red"
		}
		is DomainException.NotFoundError -> "No se encontró el elemento solicitado"
		is DomainException.ValidationError -> "Datos inválidos: $message"
		is DomainException.InvalidStateError -> "Estado inválido de la aplicación"
		is DomainException.FileAccessError -> "No se puede acceder al archivo"
		is DomainException.UnsupportedFormatError -> "Formato de archivo no soportado"
		is DomainException.PermissionDeniedError -> "Permiso denegado"
		is DomainException.UnknownError -> "Ocurrió un error inesperado"
	}
}

/**
 * Determina si el error es recuperable (puede reintentarse)
 */
fun DomainException.isRecoverable(): Boolean {
	return when (this) {
		is DomainException.NetworkError -> isConnectionError || isServerError
		is DomainException.DatabaseError -> false
		is DomainException.PermissionDeniedError -> true // Usuario puede otorgar permiso
		else -> false
	}
}