package com.pmk.freeplayer.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pmk.freeplayer.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
	
	// ==================== INSERTS & UPDATES ====================
	
	@Insert(onConflict = OnConflictStrategy.ABORT)
	suspend fun insert(user: UserEntity): Long
	
	@Update
	suspend fun update(user: UserEntity)
	
	// Actualización rápida de perfil
	@Query("""
        UPDATE users 
        SET full_name = :fullName,
            avatar_uri = :avatarUri
        WHERE user_id = :id
    """)
	suspend fun updateProfile(id: Long, fullName: String?, avatarUri: String?)
	
	// Actualización de seguridad (Password)
	@Query("""
        UPDATE users 
        SET password_hash = :newHash,
            salt = :newSalt
        WHERE user_id = :id
    """)
	suspend fun updatePassword(id: Long, newHash: String, newSalt: String?)
	
	// Actualizar última conexión
	@Query("UPDATE users SET last_login = :timestamp WHERE user_id = :id")
	suspend fun updateLastLogin(id: Long, timestamp: Long = System.currentTimeMillis())
	
	// ==================== DELETES ====================
	
	@Delete
	suspend fun delete(user: UserEntity)
	
	// Soft Delete (Desactivar en lugar de borrar)
	@Query("UPDATE users SET is_active = 0 WHERE user_id = :id")
	suspend fun deactivateUser(id: Long)
	
	@Query("DELETE FROM users WHERE user_id = :id")
	suspend fun deleteById(id: Long)
	
	// ==================== AUTHENTICATION QUERIES ====================
	
	/**
	 * Busca usuario para login (Email o Username).
	 * El repositorio se encarga de verificar el password_hash con BCrypt.
	 */
	@Query("""
        SELECT * FROM users 
        WHERE (email = :identifier OR username = :identifier) 
          AND is_active = 1 
        COLLATE NOCASE 
        LIMIT 1
    """)
	suspend fun findUserForLogin(identifier: String): UserEntity?
	
	@Query("SELECT * FROM users WHERE email = :email COLLATE NOCASE LIMIT 1")
	suspend fun findUserByEmail(email: String): UserEntity?
	
	@Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email COLLATE NOCASE)")
	suspend fun existsEmail(email: String): Boolean
	
	@Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username COLLATE NOCASE)")
	suspend fun existsUsername(username: String): Boolean
	
	// ==================== QUERIES BÁSICAS ====================
	
	@Query("SELECT * FROM users WHERE user_id = :id LIMIT 1")
	suspend fun getUserById(id: Long): UserEntity?
	
	@Query("SELECT * FROM users WHERE user_id = :id LIMIT 1")
	fun getUserByIdFlow(id: Long): Flow<UserEntity?>
	
	@Query("SELECT * FROM users ORDER BY username ASC")
	fun getAllUsers(): Flow<List<UserEntity>>
	
	// ==================== ESTADÍSTICAS DE USUARIO ====================
	
	@Query("UPDATE users SET total_plays = total_plays + 1 WHERE user_id = :id")
	suspend fun incrementPlayCount(id: Long)
	
	/**
	 * Recalcula contadores (Favoritos y Playlists).
	 * Nota: Asume que las tablas se llaman 'songs' (con is_favorite) y 'playlists'.
	 */
	@Query("""
        UPDATE users 
        SET favorite_count = (SELECT COUNT(*) FROM songs WHERE is_favorite = 1),
            total_plays = (SELECT COUNT(*) FROM playback_history WHERE user_id = users.user_id)
        WHERE user_id = :id
    """)
	suspend fun refreshUserStats(id: Long)
	
	@Transaction
	suspend fun refreshAllUserStats() {
		// Implementación genérica si fuera multi-usuario real
	}
}