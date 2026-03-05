package com.pmk.freeplayer.feature.metadata.data.mapper

import GeniusSongDto
import com.pmk.freeplayer.feature.metadata.data.local.entity.LyricsEntity
import com.pmk.freeplayer.feature.metadata.domain.model.GeniusSongResult
import com.pmk.freeplayer.feature.metadata.domain.model.LyricsData

// feature/metadata/data/mapper/GeniusMapper.kt

fun GeniusSongDto.toDomain(localConfidence: Float): GeniusSongResult = GeniusSongResult(
	geniusId            = id,
	geniusUrl           = url,
	apiPath             = apiPath,
	title               = title,
	fullTitle           = fullTitle,
	titleWithFeatured   = titleWithFeatured,
	language            = language,
	releaseDate         = releaseDate,
	recordingLocation   = recordingLocation,
	lyricsState         = lyricsState,
	songArtImageUrl     = songArtImageUrl,
	songArtThumbnailUrl = songArtThumbnailUrl,
	headerImageUrl      = headerImageUrl,
	primaryColor        = primaryColor,
	secondaryColor      = secondaryColor,
	textColor           = textColor,
	appleMusicId        = appleMusicId,
	primaryArtistId     = primaryArtist?.id,
	primaryArtistName   = primaryArtist?.name ?: primaryArtistNames,
	primaryArtistImageUrl = primaryArtist?.imageUrl,
	primaryArtistBio    = primaryArtist?.description?.plain,
	featuredArtists     = featuredArtists?.map { it.name } ?: emptyList(),
	producerArtists     = producerArtists?.map { it.name } ?: emptyList(),
	writerArtists       = writerArtists?.map { it.name } ?: emptyList(),
	albumId             = album?.id,
	albumTitle          = album?.name,
	albumCoverUrl       = album?.coverArtUrl,
	albumReleaseDate    = album?.releaseDate,
	confidenceScore     = localConfidence,
)

fun LyricsData.toEntity(): LyricsEntity = LyricsEntity(
	songId      = songId,
	plainText   = plainText,
	language    = language,
	source      = source,
	fetchedAt   = fetchedAt,
	lastUpdated = fetchedAt,
)

fun LyricsEntity.toDomain(): LyricsData = LyricsData(
	songId    = songId,
	plainText = plainText,
	language  = language,
	source    = source,
	fetchedAt = fetchedAt,
)