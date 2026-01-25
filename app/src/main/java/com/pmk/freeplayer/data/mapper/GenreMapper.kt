package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.entity.GenreEntity
import com.pmk.freeplayer.domain.model.Genre

/** 🔄 GENRE MAPPER Convierte entre la base de datos (Entity) y la UI (Domain Model). */

// ==================== ENTITY -> DOMAIN ====================

fun GenreEntity.toDomain(): Genre {
   return Genre(
      id = this.genreId,
      name = this.name,

      // --- Visual ---
      description = this.description,
      hexColor = this.hexColor,
      // Prioridad: Icono local (usuario/caché) > Remoto (API)
      iconUri = this.localIconPath ?: this.remoteIconUrl,

      // --- Stats ---
      songCount = this.songCount,
      playCount = this.playCount,

      // --- Info Extra ---
      originDecade = this.originDecade,
      originCountry = this.originCountry,
   )
}

// ==================== DOMAIN -> ENTITY ====================

fun Genre.toEntity(originalLocalPath: String? = null): GenreEntity {
   return GenreEntity(
      genreId = this.id,
      name = this.name,

      // ⚡ Generamos el nombre normalizado automáticamente (ROCK METAL)
      // Esto es crítico para los índices de búsqueda rápida
      normalizedName = this.name.trim().uppercase(),
      description = this.description,
      hexColor = this.hexColor,

      // Lógica inversa de imágenes:
      // Si la URI empieza por http, es remota. Si no, asumimos local.
      localIconPath =
         originalLocalPath ?: if (this.iconUri?.startsWith("http") == false) this.iconUri else null,
      remoteIconUrl = if (this.iconUri?.startsWith("http") == true) this.iconUri else null,

      // Stats
      songCount = this.songCount,
      playCount = this.playCount,

      // Campos que faltan en el modelo de dominio (se ponen a 0 o se mantienen si fuera update)
      artistCount = 0,
      albumCount = 0,
      originDecade = this.originDecade,
      originCountry = this.originCountry,
   )
}

// ==================== LISTAS ====================

fun List<GenreEntity>.toDomain(): List<Genre> = map { it.toDomain() }
