package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.entity.PlaybackHistoryEntity
import com.pmk.freeplayer.domain.model.PlaybackHistory
import com.pmk.freeplayer.domain.model.TrackDuration
import com.pmk.freeplayer.domain.model.enums.AudioOutput
import com.pmk.freeplayer.domain.model.enums.PlaybackSource
import java.util.Calendar

/** 🔄 PLAYBACK HISTORY MAPPER Convierte entre la base de datos (Entity) y la UI (Domain Model). */

// ==================== ENTITY -> DOMAIN ====================

fun PlaybackHistoryEntity.toDomain(): PlaybackHistory {
   return PlaybackHistory(
      id = this.historyId,
      songId = this.songId,
      timestamp = this.timestamp,

      // Value Class
      playedDurationMs = TrackDuration(this.playedDurationMs),
      totalDurationMs = this.totalDurationMs,
      isCompleted = this.isCompleted,

      // Enums seguros
      source = mapStringToSource(this.source),
      contextName = this.contextName,

      // Técnico
      outputType = mapStringToOutputType(this.outputType),
      volume = this.volume,

      // Analytics
      pauseCount = this.pauseCount,
      seekCount = this.seekCount,
   )
}

// ==================== DOMAIN -> ENTITY ====================

fun PlaybackHistory.toEntity(): PlaybackHistoryEntity {
   // Calculamos metadatos temporales necesarios para la Entidad
   val calendar = Calendar.getInstance().apply { timeInMillis = this@toEntity.timestamp }

   return PlaybackHistoryEntity(
      historyId = this.id,
      songId = this.songId,
      timestamp = this.timestamp,
      playedDurationMs = this.playedDurationMs.millis, // Extraemos el Long del Value Class
      totalDurationMs = this.totalDurationMs,

      // Recálculo básico
      completionRatio =
         if (totalDurationMs > 0) this.playedDurationMs.millis.toFloat() / totalDurationMs else 0f,
      isCompleted = this.isCompleted,
      source = this.source.name,
      contextName = this.contextName,

      // Mapeo inverso de Enums
      outputType = this.outputType.name,
      volume = this.volume,
      pauseCount = this.pauseCount,
      seekCount = this.seekCount,

      // --- Campos obligatorios en Entity que no están en Domain ---
      // (Usamos valores por defecto o calculados)
      playbackMode = "NORMAL", // Default seguro
      hourOfDay = calendar.get(Calendar.HOUR_OF_DAY),
      dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK),
      userId = 1L, // Default para app offline
   )
}

// ==================== LISTAS ====================

fun List<PlaybackHistoryEntity>.toDomain(): List<PlaybackHistory> = map { it.toDomain() }

// ==================== HELPERS PRIVADOS ====================

private fun mapStringToSource(sourceStr: String): PlaybackSource {
   return try {
      PlaybackSource.valueOf(sourceStr.uppercase())
   } catch (e: Exception) {
      PlaybackSource.LIBRARY // Fallback por defecto
   }
}

private fun mapStringToOutputType(typeStr: String?): AudioOutput {
   if (typeStr == null) return AudioOutput.UNKNOWN
   return try {
      AudioOutput.valueOf(typeStr.uppercase())
   } catch (e: Exception) {
      AudioOutput.UNKNOWN
   }
}
