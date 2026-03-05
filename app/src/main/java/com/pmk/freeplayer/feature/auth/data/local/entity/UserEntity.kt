package com.pmk.freeplayer.feature.auth.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representación en base de datos del usuario autenticado.
 *
 * Responsabilidad exclusiva: identidad, credenciales y perfil.
 * Los contadores de estadísticas (plays, favoritos, playlists) se eliminaron
 * de esta entidad — pertenecen a [feature.statistics] y se calculan
 * desde sus propias tablas sin caché aquí.
 */
@Entity(
	tableName = "users",
	indices = [
		Index(value = ["username"], unique = true),
		Index(value = ["email"], unique = true),
	]
)
data class UserEntity(
	
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "user_id")
	val userId: Long = 0,
	
	// ═══════════════════════════════════════════════════════════════
	// IDENTIDAD
	// ═══════════════════════════════════════════════════════════════
	
	@ColumnInfo(name = "username")
	val username: String,
	
	@ColumnInfo(name = "email")
	val email: String,
	
	// ═══════════════════════════════════════════════════════════════
	// SEGURIDAD — Solo para autenticación LOCAL
	// Nullable porque los usuarios OAuth no tienen contraseña local.
	// ═══════════════════════════════════════════════════════════════
	
	@ColumnInfo(name = "password_hash")
	val passwordHash: String? = null,
	
	@ColumnInfo(name = "salt")
	val salt: String? = null,
	
	// ═══════════════════════════════════════════════════════════════
	// PERFIL
	// ═══════════════════════════════════════════════════════════════
	
	@ColumnInfo(name = "full_name")
	val fullName: String? = null,
	
	@ColumnInfo(name = "avatar_uri")
	val avatarUri: String? = null,
	
	// ═══════════════════════════════════════════════════════════════
	// AUTENTICACIÓN
	// ═══════════════════════════════════════════════════════════════
	
	/** Valores posibles: "LOCAL", "GOOGLE", "FACEBOOK". */
	@ColumnInfo(name = "auth_type")
	val authType: String = "LOCAL",
	
	/** ID del usuario en el proveedor externo (Google UID, Facebook ID). */
	@ColumnInfo(name = "external_id")
	val externalId: String? = null,
	
	// ═══════════════════════════════════════════════════════════════
	// ESTADO
	// ═══════════════════════════════════════════════════════════════
	
	@ColumnInfo(name = "is_active")
	val isActive: Boolean = true,
	
	@ColumnInfo(name = "last_login")
	val lastLogin: Long = System.currentTimeMillis(),
	
	@ColumnInfo(name = "created_at")
	val createdAt: Long = System.currentTimeMillis(),
)