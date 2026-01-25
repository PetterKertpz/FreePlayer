package com.pmk.freeplayer.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "users",
	indices = [
		Index(value = ["username"], unique = true),
		Index(value = ["email"], unique = true)
	]
)
data class UserEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "user_id") val userId: Long = 0,
	
	// ==================== IDENTIDAD ====================
	@ColumnInfo(name = "username") val username: String,
	@ColumnInfo(name = "email") val email: String,
	
	// Hash de la contraseña (BCrypt).
	// NULLABLE: Porque si entra con Google, no tiene contraseña local.
	@ColumnInfo(name = "password_hash") val passwordHash: String? = null,
	@ColumnInfo(name = "salt") val salt: String? = null,
	
	// ==================== PERFIL (Lo que se ve en la UI) ====================
	@ColumnInfo(name = "full_name") val fullName: String? = null,
	
	// URI local o URL remota
	@ColumnInfo(name = "avatar_uri") val avatarUri: String? = null,
	@ColumnInfo(name = "birth_date") val birthDate: Long? = null,
	
	// ==================== AUTENTICACIÓN ====================
	@ColumnInfo(name = "auth_type") val authType: String = "LOCAL", // "LOCAL", "GOOGLE"
	@ColumnInfo(name = "external_id") val externalId: String? = null, // ID de Google
	
	// ==================== ESTADO ====================
	@ColumnInfo(name = "is_active") val isActive: Boolean = true,
	@ColumnInfo(name = "last_login") val lastLogin: Long = System.currentTimeMillis(),
	@ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
	
	// ==================== CACHÉ DE ESTADÍSTICAS ====================
	// Solo para mostrar "Resumen" rápido en el perfil.
	@ColumnInfo(name = "total_plays") val totalPlays: Int = 0,
	@ColumnInfo(name = "favorite_count") val favoriteCount: Int = 0,
	@ColumnInfo(name = "playlist_count") val playlistCount: Int = 0
)