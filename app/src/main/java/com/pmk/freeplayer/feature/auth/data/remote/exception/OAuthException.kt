package com.pmk.freeplayer.feature.auth.data.remote.exception

/**
 * Errores tipados del flujo OAuth.
 *
 * Permite al ViewModel reaccionar distinto según el tipo de error:
 * - [Cancelled]      → no mostrar error, el usuario cerró voluntariamente
 * - [NoAccountFound] → sugerir agregar una cuenta Google al dispositivo
 * - [Unknown]        → mostrar mensaje genérico de error
 */
sealed class OAuthException(message: String) : Exception(message) {
	
	/** El usuario cerró el selector de cuentas sin elegir ninguna. */
	data object Cancelled : OAuthException("Sign-in cancelled by user") {
		private fun readResolve(): Any = Cancelled
	}
	
	/** El dispositivo no tiene ninguna cuenta Google configurada. */
	data object NoAccountFound : OAuthException("No Google account found on device") {
		private fun readResolve(): Any = NoAccountFound
	}
	
	/** El tipo de credencial retornado no es el esperado. */
	data class UnexpectedCredentialType(val type: String) :
		OAuthException("Unexpected credential type: $type")
	
	/** Error genérico con mensaje descriptivo. */
	data class Unknown(val detail: String) : OAuthException(detail)
}