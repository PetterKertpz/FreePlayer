package com.pmk.freeplayer.data.repository

import com.pmk.freeplayer.data.local.dao.EnrichmentResultDao
import com.pmk.freeplayer.data.local.dao.LyricsDao
import com.pmk.freeplayer.data.local.dao.SongDao
import com.pmk.freeplayer.data.local.entity.EnrichmentResultEntity
import com.pmk.freeplayer.data.remote.scraper.GeniusScraperDataSource
import com.pmk.freeplayer.data.remote.source.GeniusRemoteDataSource
import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.scanner.EnrichmentResult
import com.pmk.freeplayer.domain.strategy.SongMatchingStrategy
import com.pmk.freeplayer.domain.strategy.SongMetadata
import javax.inject.Inject

class EnrichmentRepositoryImpl
@Inject
constructor(
	private val apiSource: GeniusRemoteDataSource,
	private val scraperSource: GeniusScraperDataSource,
	private val lyricsDao: LyricsDao,
	private val songDao: SongDao,
	private val enrichmentDao: EnrichmentResultDao, // ¡Necesario para guardar historial!
	private val matchingStrategy: SongMatchingStrategy,
) {

   suspend fun enrichSong(song: Song): EnrichmentResult {
      val startTime = System.currentTimeMillis()

      // 1. Verificar caché local (Evitar llamadas innecesarias)
      if (lyricsDao.hasLyrics(song.id)) {
         return buildAndSaveResult(
            songId = song.id,
            startTime = startTime,
            success = true,
            msg = "Letra ya existía localmente",
            score = 1.0f,
            lyricsFound = true,
            updated = false,
         )
      }

      return try {
         // 2. Buscar en API Remota
         // Usamos cleanQuery para aumentar probabilidad de éxito (quitar "Remix", "Feat", etc si
         // falla primero)
         val geniusDto =
            apiSource.searchSong(song.artistName, song.title)
               ?: return buildAndSaveResult(
                  song.id,
                  startTime,
                  false,
                  "No encontrado en Genius API",
               )

         // 3. Preparar Metadatos para la Estrategia
         val localMeta = SongMetadata.fromSong(song)
         val remoteMeta =
            SongMetadata(
               title = geniusDto.title,
               artist = geniusDto.artist,
               album = geniusDto.album, // Puede ser null
               duration = null,
               year = null,
            )

         // 4. Calcular Match (Usando pesos dinámicos)
         val matchResult = matchingStrategy.calculateMatch(localMeta, remoteMeta)

         if (!matchResult.esConfiable) {
            return buildAndSaveResult(
               songId = song.id,
               startTime = startTime,
               success = false,
               msg =
                  "Coincidencia baja: ${String.format("%.2f", matchResult.puntuacionTotal)} (T:${matchResult.puntuacionTitulo}, A:${matchResult.puntuacionArtista})",
               score = matchResult.puntuacionTotal,
            )
         }

         // 5. Scraping (Obtener letra real desde la URL)
         val lyricsText = scraperSource.fetchLyricsFromUrl(geniusDto.url)

         if (lyricsText.isNullOrBlank()) {
            return buildAndSaveResult(
               songId = song.id,
               startTime = startTime,
               success = false,
               msg = "Scraping fallido: URL accesible pero sin texto",
               score = matchResult.puntuacionTotal,
            )
         }

         // 6. Guardar Letra en DB
         geniusDto.plainLyrics = lyricsText
         // Aquí geniusDto ya tiene el método toEntity(songId) que definiste antes
         lyricsDao.insert(geniusDto.toEntity(song.id))
	      
	      songDao.markAsEnriched(
		      songId = song.id,
		      hasLyrics = true,
		      geniusId = geniusDto.id.toString(),
		      geniusUrl = geniusDto.url,
		      confidence = matchResult.puntuacionTotal
	      )
         // 7. Retornar Éxito
         return buildAndSaveResult(
            songId = song.id,
            startTime = startTime,
            success = true,
            msg = null,
            score = matchResult.puntuacionTotal,
            lyricsFound = true,
            updated = true,
         )
      } catch (e: Exception) {
         e.printStackTrace()
         buildAndSaveResult(song.id, startTime, false, "Excepción: ${e.message}")
      }
   }

   /** Helper para construir el objeto de dominio Y guardar la entidad de historial en BD */
   private suspend fun buildAndSaveResult(
      songId: Long,
      startTime: Long,
      success: Boolean,
      msg: String? = null,
      score: Float? = null,
      lyricsFound: Boolean = false,
      updated: Boolean = false,
   ): EnrichmentResult {

      // Mapeo manual a Entity para guardar en Room
      val entity =
         EnrichmentResultEntity(
            cancionId = songId,
            fecha = startTime,
            exitoso = success,
            datosActualizados = updated,
            letraEncontrada = lyricsFound,
            nivelCoincidencia = score,
            error = msg,
         )

      // Guardamos el log de la operación
      enrichmentDao.insert(entity)

      // Retornamos el modelo de dominio
      return EnrichmentResult(
         cancionId = songId,
         fecha = startTime,
         exitoso = success,
         datosActualizados = updated,
         letraEncontrada = lyricsFound,
         nivelCoincidencia = score,
         error = msg,
      )
   }
}
