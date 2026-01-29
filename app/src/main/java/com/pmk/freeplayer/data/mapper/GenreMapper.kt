package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.entity.GenreEntity
import com.pmk.freeplayer.domain.model.Genre

/**
 * 📄 GENRE MAPPER
 *
 * Convierte entre la capa de datos (GenreEntity) y el modelo de dominio (Genre).
 *
 * **Lógica de Iconos:**
 * - Entity tiene dos campos: `localIconPath` (archivo local) y `remoteIconUrl` (URL API)
 * - Domain tiene un solo campo: `iconUri` que puede ser local o remoto
 * - Prioridad: Local > Remoto (mejor rendimiento y disponibilidad offline)
 *
 * **Normalización:**
 * - `normalizedName` se genera automáticamente en UPPERCASE para búsquedas rápidas
 * - Ejemplo: "Rock Metal" -> "ROCK METAL"
 */

// ══════════════════════════════════════════════════════════════════════════════
// ENTITY -> DOMAIN (Para presentar en la UI)
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Convierte [GenreEntity] a [Genre] para uso en la UI.
 *
 * **Prioridad de iconos:** Local > Remoto
 */
fun GenreEntity.toDomain(): Genre {
	return Genre(
		id = this.genreId,
		name = this.name,
		
		// Visual
		description = this.description,
		hexColor = this.hexColor,
		// Prioriza el icono local (más rápido y funciona offline)
		iconUri = this.localIconPath ?: this.remoteIconUrl,
		
		// Stats
		songCount = this.songCount,
		playCount = this.playCount,
		
		// Metadata extra
		originDecade = this.originDecade,
		originCountry = this.originCountry
	)
}

/**
 * Convierte una lista de [GenreEntity] a [Genre].
 */
fun List<GenreEntity>.toDomain(): List<Genre> = map { it.toDomain() }

// ══════════════════════════════════════════════════════════════════════════════
// DOMAIN -> ENTITY (Para guardar en la base de datos)
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Convierte [Genre] a [GenreEntity] para inserción en base de datos.
 *
 * **Uso:** Para crear nuevos géneros o insertar desde cero.
 *
 * @param preserveLocalIcon Si existe un path local previo, preservarlo en lugar de intentar detectarlo
 * @return Nueva entidad con timestamps actuales y normalización automática
 */
fun Genre.toEntity(preserveLocalIcon: String? = null): GenreEntity {
	val currentTime = System.currentTimeMillis()
	
	// Detecta si la URI es remota (HTTP/HTTPS) o local
	val isRemoteIcon = this.iconUri?.startsWith("http", ignoreCase = true) == true
	
	return GenreEntity(
		genreId = this.id,
		name = this.name.trim(),
		
		// ⚡ Normalización automática para índices de búsqueda
		normalizedName = this.name.trim().uppercase(),
		
		description = this.description?.trim(),
		hexColor = this.hexColor,
		
		// Lógica de iconos: separa local vs remoto
		localIconPath = preserveLocalIcon ?: if (!isRemoteIcon) this.iconUri else null,
		remoteIconUrl = if (isRemoteIcon) this.iconUri else null,
		
		// Stats (los que vienen del domain)
		songCount = this.songCount,
		playCount = this.playCount,
		
		// Stats adicionales del Entity (se inicializan en 0 para nuevos registros)
		artistCount = 0,
		albumCount = 0,
		
		// Metadata
		originDecade = this.originDecade,
		originCountry = this.originCountry,
		
		// Timestamps
		dateAdded = currentTime,
		lastUpdated = currentTime
	)
}

/**
 * Convierte una lista de [Genre] a [GenreEntity].
 */
fun List<Genre>.toEntity(): List<GenreEntity> = map { it.toEntity() }

// ══════════════════════════════════════════════════════════════════════════════
// ACTUALIZACIÓN DE ENTIDADES EXISTENTES
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Actualiza una [GenreEntity] existente con datos de [Genre].
 *
 * **Uso:** Para actualizar registros manteniendo campos que no están en el domain model.
 *
 * Preserva:
 * - `dateAdded` (fecha de creación original)
 * - `artistCount` y `albumCount` (calculados por la app)
 * - `localIconPath` existente (a menos que se especifique uno nuevo)
 *
 * Actualiza:
 * - `lastUpdated` (se marca como modificado ahora)
 * - Todos los campos del domain model
 *
 * @param existingEntity La entidad actual en la base de datos
 * @param newLocalIcon Nuevo path de icono local (opcional)
 * @return Entidad actualizada lista para guardar
 */
fun Genre.toUpdatedEntity(
	existingEntity: GenreEntity,
	newLocalIcon: String? = null
): GenreEntity {
	val isRemoteIcon = this.iconUri?.startsWith("http", ignoreCase = true) == true
	
	return GenreEntity(
		genreId = this.id,
		name = this.name.trim(),
		normalizedName = this.name.trim().uppercase(),
		description = this.description?.trim(),
		hexColor = this.hexColor,
		
		// Prioriza nuevo icono local > existente > detección automática
		localIconPath = newLocalIcon
			?: existingEntity.localIconPath
			?: if (!isRemoteIcon) this.iconUri else null,
		remoteIconUrl = if (isRemoteIcon) this.iconUri else existingEntity.remoteIconUrl,
		
		// Stats del domain
		songCount = this.songCount,
		playCount = this.playCount,
		
		// Preserva stats del entity (calculados por la app)
		artistCount = existingEntity.artistCount,
		albumCount = existingEntity.albumCount,
		
		// Metadata
		originDecade = this.originDecade,
		originCountry = this.originCountry,
		
		// Timestamps: preserva fecha de creación, actualiza modificación
		dateAdded = existingEntity.dateAdded,
		lastUpdated = System.currentTimeMillis()
	)
}

/**
 * Actualiza solo las estadísticas de una entidad existente.
 *
 * **Uso:** Cuando solo cambias contadores sin modificar otros datos.
 *
 * @param songCount Nuevo conteo de canciones (null = mantener actual)
 * @param artistCount Nuevo conteo de artistas (null = mantener actual)
 * @param albumCount Nuevo conteo de álbumes (null = mantener actual)
 * @param playCount Nuevo conteo de reproducciones (null = mantener actual)
 */
fun GenreEntity.updateStats(
	songCount: Int? = null,
	artistCount: Int? = null,
	albumCount: Int? = null,
	playCount: Int? = null
): GenreEntity {
	return this.copy(
		songCount = songCount ?: this.songCount,
		artistCount = artistCount ?: this.artistCount,
		albumCount = albumCount ?: this.albumCount,
		playCount = playCount ?: this.playCount,
		lastUpdated = System.currentTimeMillis()
	)
}

/**
 * Actualiza solo el icono local de una entidad.
 *
 * **Uso:** Cuando descargas/cacheas una imagen remota.
 *
 * @param localPath Ruta del archivo local guardado
 */
fun GenreEntity.updateLocalIcon(localPath: String): GenreEntity {
	return this.copy(
		localIconPath = localPath,
		lastUpdated = System.currentTimeMillis()
	)
}

// ══════════════════════════════════════════════════════════════════════════════
// UTILIDADES
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Crea una [GenreEntity] básica a partir de un nombre.
 *
 * **Uso:** Para creación rápida desde tags de archivos de audio.
 *
 * @param genreName Nombre del género (será normalizado automáticamente)
 * @return Entidad básica con valores por defecto
 */
fun createBasicGenreEntity(genreName: String): GenreEntity {
	val cleanName = genreName.trim()
	val currentTime = System.currentTimeMillis()
	
	return GenreEntity(
		genreId = 0, // AutoGenerate
		name = cleanName,
		normalizedName = cleanName.uppercase(),
		description = null,
		hexColor = null,
		remoteIconUrl = null,
		localIconPath = null,
		songCount = 1, // Al menos una canción que lo creó
		artistCount = 0,
		albumCount = 0,
		playCount = 0,
		originDecade = null,
		originCountry = null,
		dateAdded = currentTime,
		lastUpdated = currentTime
	)
}

/**
 * Verifica si una entidad tiene icono (local o remoto).
 */
val GenreEntity.hasIcon: Boolean
	get() = !localIconPath.isNullOrBlank() || !remoteIconUrl.isNullOrBlank()

/**
 * Verifica si el género tiene estadísticas significativas.
 */
val GenreEntity.hasContent: Boolean
	get() = songCount > 0 || artistCount > 0 || albumCount > 0