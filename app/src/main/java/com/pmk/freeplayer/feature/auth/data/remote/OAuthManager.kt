package com.pmk.freeplayer.feature.auth.data.remote

import android.content.Context
import com.pmk.freeplayer.feature.auth.data.remote.model.OAuthUser

/**
 * Contrato para autenticación con proveedores externos.
 *
 * Dos métodos:
 * - [verifyGoogleToken]  → recibe idToken ya obtenido (tests / headless)
 * - [launchGoogleSignIn] → lanza el Credential Manager picker (flujo UI real)
 */
interface OAuthManager {
	
	/**
	 * Verifica un idToken de Google ya obtenido contra Firebase.
	 * Útil para tests o cuando el token viene de otra fuente.
	 */
	suspend fun verifyGoogleToken(idToken: String): OAuthUser
	
	/**
	 * Lanza el selector de cuentas de Google via Credential Manager.
	 *
	 * @param activityContext Contexto de la Activity activa.
	 *        Credential Manager requiere Activity — NO pasar ApplicationContext.
	 * @throws com.pmk.freeplayer.feature.auth.data.remote.exception.OAuthException según el tipo de fallo.
	 */
	suspend fun launchGoogleSignIn(activityContext: Context): OAuthUser
}