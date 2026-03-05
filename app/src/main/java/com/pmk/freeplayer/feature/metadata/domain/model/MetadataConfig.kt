package com.pmk.freeplayer.feature.metadata.domain.model// feature/metadata/domain/model/MetadataConfig.kt

data class MetadataConfig(
	val writeMetadataToFile : Boolean = false,
	val geniusAccessToken   : String? = null,       // null → scraping mode
	val similarityThreshold : Float   = 0.75f,
	val autoSearchEnabled   : Boolean = true,       // delegado desde Settings.autoSearchLyrics
) {
	val usesApi: Boolean get() = !geniusAccessToken.isNullOrBlank()
}