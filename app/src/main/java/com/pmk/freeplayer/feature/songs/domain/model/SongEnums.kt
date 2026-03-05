package com.pmk.freeplayer.feature.songs.domain.model

enum class VersionType {
   ORIGINAL,
   REMIX,
   LIVE,
   ACOUSTIC,
   COVER,
   RADIO_EDIT,
   INSTRUMENTAL,
   DEMO,
}

enum class SourceType {
   LOCAL,
   DOWNLOAD,
}

enum class AudioQuality {
   LOW,
   MEDIUM,
   HIGH,
   LOSSLESS,
   HI_RES,
   UNKNOWN,
}

sealed interface MetadataStatus {
	data object Raw      : MetadataStatus
	data object Clean    : MetadataStatus
	data object Enriched : MetadataStatus
	data object NotFound : MetadataStatus  // Genius no retornó match válido
	data object Skipped  : MetadataStatus  // excluido manualmente por el usuario
	data class  Failed(val attempt: Int = 1) : MetadataStatus  // reintentable (1–3)
	
	companion object {
		fun from(value: String): MetadataStatus = when {
			value == "CLEAN"           -> Clean
			value == "ENRICHED"        -> Enriched
			value == "NOT_FOUND"       -> NotFound
			value == "SKIPPED"         -> Skipped
			value.startsWith("FAILED") -> {
				val attempt = value.substringAfter("FAILED_", "1").toIntOrNull() ?: 1
				Failed(attempt)
			}
			else -> Raw
		}
		
		fun MetadataStatus.storageKey(): String = when (this) {
			Raw      -> "RAW"
			Clean    -> "CLEAN"
			Enriched -> "ENRICHED"
			NotFound -> "NOT_FOUND"
			Skipped  -> "SKIPPED"
			is Failed -> "FAILED_${attempt}"
		}
		
		fun MetadataStatus.isTerminal(): Boolean =
			this is Enriched || this is NotFound || this is Skipped
		
		fun MetadataStatus.isRetryable(): Boolean =
			this is Failed && attempt < 3
	}
}
