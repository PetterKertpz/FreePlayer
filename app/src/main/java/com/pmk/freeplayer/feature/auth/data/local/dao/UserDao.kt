package com.pmk.freeplayer.feature.auth.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pmk.freeplayer.feature.auth.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO de la tabla `users`.
 *
 * Responsabilidad exclusiva: CRUD de identidad, credenciales y perfil.
 * Se eliminaron las queries que cruzaban a tablas de otras features
 * (songs.is_favorite, playback_history) — esas consultas pertenecen
 * a sus propios DAOs en [feature.statistics] y [feature.favorites].
 */
@Dao
interface UserDao {
	
	// ═══════════════════════════════════════════════════════════════
	// INSERTS
	// ═══════════════════════════════════════════════════════════════
	
	/** Retorna el id autogenerado. ABORT si username o email ya existen. */
	@Insert(onConflict = OnConflictStrategy.ABORT)
	suspend fun insert(user: UserEntity): Long
	
	// ═══════════════════════════════════════════════════════════════
	// UPDATES
	// ═══════════════════════════════════════════════════════════════
	
	@Update
	suspend fun update(user: UserEntity)
	
	@Query(
		"""
        UPDATE users
        SET full_name  = :fullName,
            avatar_uri = :avatarUri
        WHERE user_id = :id
        """
	)
	suspend fun updateProfile(id: Long, fullName: String?, avatarUri: String?)
	
	@Query(
		"""
        UPDATE users
        SET password_hash = :newHash,
            salt          = :newSalt
        WHERE user_id = :id
        """
	)
	suspend fun updatePassword(id: Long, newHash: String, newSalt: String?)
	
	@Query("UPDATE users SET last_login = :timestamp WHERE user_id = :id")
	suspend fun updateLastLogin(id: Long, timestamp: Long = System.currentTimeMillis())
	
	@Query("UPDATE users SET username = :username WHERE user_id = :id")
	suspend fun updateUsername(id: Long, username: String)
	
	// ═══════════════════════════════════════════════════════════════
	// DELETES
	// ═══════════════════════════════════════════════════════════════
	
	/** Soft delete: mantiene el registro pero desactiva la cuenta. */
	@Query("UPDATE users SET is_active = 0 WHERE user_id = :id")
	suspend fun deactivateUser(id: Long)
	
	/** Hard delete: elimina el registro permanentemente. */
	@Query("DELETE FROM users WHERE user_id = :id")
	suspend fun deleteById(id: Long)
	
	// ═══════════════════════════════════════════════════════════════
	// AUTENTICACIÓN
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * Busca usuario activo por email o username para el flujo de login.
	 * La verificación del password_hash se hace en el repositorio con BCrypt.
	 */
	@Query(
		"""
        SELECT * FROM users
        WHERE (email = :identifier OR username = :identifier)
          AND is_active = 1
        COLLATE NOCASE
        LIMIT 1
        """
	)
	suspend fun findForLogin(identifier: String): UserEntity?
	
	/** Busca usuario por proveedor externo para login con OAuth. */
	@Query(
		"""
        SELECT * FROM users
        WHERE external_id = :externalId
          AND auth_type   = :authType
          AND is_active   = 1
        LIMIT 1
        """
	)
	suspend fun findByExternalId(externalId: String, authType: String): UserEntity?
	
	@Query("SELECT * FROM users WHERE email = :email COLLATE NOCASE LIMIT 1")
	suspend fun findByEmail(email: String): UserEntity?
	
	// ═══════════════════════════════════════════════════════════════
	// VALIDACIONES
	// ═══════════════════════════════════════════════════════════════
	
	@Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email COLLATE NOCASE)")
	suspend fun existsEmail(email: String): Boolean
	
	@Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username COLLATE NOCASE)")
	suspend fun existsUsername(username: String): Boolean
	
	// ═══════════════════════════════════════════════════════════════
	// QUERIES
	// ═══════════════════════════════════════════════════════════════
	
	@Query("SELECT * FROM users WHERE user_id = :id LIMIT 1")
	suspend fun getById(id: Long): UserEntity?
	
	@Query("SELECT * FROM users WHERE user_id = :id LIMIT 1")
	fun getByIdFlow(id: Long): Flow<UserEntity?>
}