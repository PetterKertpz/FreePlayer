package com.pmk.freeplayer.feature.auth.data.remote

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.pmk.freeplayer.feature.auth.data.remote.exception.OAuthException
import com.pmk.freeplayer.feature.auth.data.remote.model.OAuthUser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

/**
 * Implementación de [OAuthManager] usando Credential Manager (API moderna).
 *
 * Reemplaza el enfoque antiguo de GoogleSignInClient por el nuevo
 * Credential Manager de Android, que soporta:
 * - Google Sign-In con One Tap
 * - Passkeys (futuro)
 * - Contraseñas guardadas
 *
 * Flujo completo:
 * 1. LoginScreen llama al launcher de Credential Manager.
 * 2. El sistema muestra el selector de cuenta de Google.
 * 3. Usuario selecciona cuenta → se obtiene GoogleIdTokenCredential.
 * 4. Se extrae el idToken → Firebase verifica → retorna OAuthUser.
 */
class OAuthManagerImpl @Inject constructor(
	@ApplicationContext private val context: Context,
	private val firebaseAuth: FirebaseAuth,
) : OAuthManager {
	
	private val credentialManager = CredentialManager.create(context)
	
	// ═══════════════════════════════════════════════════════════════
	// INTERFACE — compatibilidad con UserRepositoryImpl
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun verifyGoogleToken(idToken: String): OAuthUser =
		signInWithFirebase(idToken)
	
	// ═══════════════════════════════════════════════════════════════
	// CREDENTIAL MANAGER — flujo real iniciado desde la UI
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * Lanza el selector de cuentas de Google y retorna el [OAuthUser].
	 *
	 * @param activityContext Contexto de la Activity activa.
	 *        Credential Manager requiere una Activity para mostrar el UI.
	 *        NO pasar ApplicationContext aquí.
	 *
	 * Uso desde LoginViewModel:
	 * ```kotlin
	 * val oAuthUser = oAuthManager.launchGoogleSignIn(activityContext)
	 * ```
	 *
	 * @throws com.pmk.freeplayer.feature.auth.data.remote.exception.OAuthException.Cancelled      si el usuario cierra el selector.
	 * @throws com.pmk.freeplayer.feature.auth.data.remote.exception.OAuthException.NoAccountFound si no hay cuentas Google en el dispositivo.
	 * @throws com.pmk.freeplayer.feature.auth.data.remote.exception.OAuthException.Unknown        para cualquier otro error.
	 */
	override suspend fun launchGoogleSignIn(activityContext: Context): OAuthUser {
		val googleIdOption = GetGoogleIdOption.Builder()
			.setFilterByAuthorizedAccounts(false) // Muestra todas las cuentas del dispositivo
			.setServerClientId(WEB_CLIENT_ID)
			.setAutoSelectEnabled(false)           // Siempre muestra el picker
			.setNonce(generateNonce())             // Protege contra replay attacks
			.build()
		
		val request = GetCredentialRequest.Builder()
			.addCredentialOption(googleIdOption)
			.build()
		
		return try {
			val result = credentialManager.getCredential(
				request = request,
				context = activityContext,
			)
			
			val credential = result.credential
			
			if (credential is CustomCredential &&
				credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
			) {
				val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
				signInWithFirebase(googleCredential.idToken)
			} else {
				throw OAuthException.UnexpectedCredentialType(credential.type)
			}
			
		} catch (e: GetCredentialCancellationException) {
			throw OAuthException.Cancelled
		} catch (e: NoCredentialException) {
			throw OAuthException.NoAccountFound
		} catch (e: GetCredentialException) {
			throw OAuthException.Unknown(e.message ?: "Credential Manager error")
		}
	}
	
	// ═══════════════════════════════════════════════════════════════
	// FIREBASE — verificación del idToken
	// ═══════════════════════════════════════════════════════════════
	
	private suspend fun signInWithFirebase(idToken: String): OAuthUser {
		val credential = GoogleAuthProvider.getCredential(idToken, null)
		val result = firebaseAuth.signInWithCredential(credential).await()
		
		val firebaseUser = result.user
			?: throw OAuthException.Unknown("Firebase returned null user after sign-in")
		
		return OAuthUser(
			externalId = firebaseUser.uid,
			email = firebaseUser.email
				?: throw OAuthException.Unknown("Google account has no email"),
			fullName = firebaseUser.displayName,
			avatarUri = firebaseUser.photoUrl?.toString(),
		)
	}
	
	// ═══════════════════════════════════════════════════════════════
	// HELPERS
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * Genera un nonce aleatorio hasheado con SHA-256.
	 * Requerido por Google para proteger contra replay attacks.
	 */
	private fun generateNonce(): String {
		val rawNonce = UUID.randomUUID().toString()
		val digest = MessageDigest.getInstance("SHA-256")
		return digest.digest(rawNonce.toByteArray())
			.fold("") { str, byte -> str + "%02x".format(byte) }
	}
	
	companion object {
		/**
		 * Web Client ID de Google Cloud Console.
		 *
		 * Ubicación: Cloud Console → APIs y servicios → Credenciales
		 * → OAuth 2.0 Client IDs → "Web client (auto created by Google Service)"
		 *
		 * ⚠️ En producción mover a local.properties:
		 *    WEB_CLIENT_ID=xxxx.apps.googleusercontent.com
		 * Y leerlo vía BuildConfig.
		 */
		private const val WEB_CLIENT_ID =
			"20757731563-lhov4b1td7q0tt9r2imt9p8voe8dmcn7.apps.googleusercontent.com"
	}
}