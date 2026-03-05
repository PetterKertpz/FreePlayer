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
   data object Raw : MetadataStatus

   data object Clean : MetadataStatus

   data object Enriched : MetadataStatus

   companion object {
      fun from(value: String): MetadataStatus =
         when (value.uppercase()) {
            "CLEAN" -> Clean
            "ENRICHED" -> Enriched
            else -> Raw
         }

      fun MetadataStatus.storageKey(): String =
         when (this) {
            Raw -> "RAW"
            Clean -> "CLEAN"
            Enriched -> "ENRICHED"
         }
   }
}
