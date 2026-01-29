package com.pmk.freeplayer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pmk.freeplayer.data.local.entity.CleaningResultEntity
import com.pmk.freeplayer.data.local.entity.EnrichmentResultEntity
import com.pmk.freeplayer.data.local.entity.ScanResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScannerDao {

   // ═══════════════════════════════════════════════════════════════
   // SCAN
   // ═══════════════════════════════════════════════════════════════

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insertScanResult(entity: ScanResultEntity): Long

   @Query("SELECT * FROM scan_results ORDER BY fecha DESC LIMIT :limit")
   fun getScanHistory(limit: Int): Flow<List<ScanResultEntity>>

   @Query("SELECT * FROM scan_results ORDER BY fecha DESC LIMIT 1")
   suspend fun getLastScan(): ScanResultEntity?

   @Query(
      """
        DELETE FROM scan_results
        WHERE id NOT IN (SELECT id FROM scan_results ORDER BY fecha DESC LIMIT :keepLast)
    """
   )
   suspend fun clearScanHistory(keepLast: Int)

   // ═══════════════════════════════════════════════════════════════
   // CLEANING
   // ═══════════════════════════════════════════════════════════════

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insertCleaningResult(entity: CleaningResultEntity): Long

   @Query("SELECT * FROM cleaning_results ORDER BY fecha DESC LIMIT :limit")
   fun getCleaningHistory(limit: Int): Flow<List<CleaningResultEntity>>

   @Query("SELECT * FROM cleaning_results ORDER BY fecha DESC LIMIT 1")
   suspend fun getLastCleaning(): CleaningResultEntity?

   @Query(
      """
        DELETE FROM cleaning_results
        WHERE id NOT IN (SELECT id FROM cleaning_results ORDER BY fecha DESC LIMIT :keepLast)
    """
   )
   suspend fun clearCleaningHistory(keepLast: Int)

   // ═══════════════════════════════════════════════════════════════
   // ENRICHMENT
   // ═══════════════════════════════════════════════════════════════

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insertEnrichmentResult(entity: EnrichmentResultEntity): Long

   @Query("SELECT * FROM enrichment_results ORDER BY fecha DESC LIMIT :limit")
   fun getEnrichmentHistory(limit: Int): Flow<List<EnrichmentResultEntity>>

   @Query("SELECT * FROM enrichment_results ORDER BY fecha DESC LIMIT 1")
   suspend fun getLastEnrichment(): EnrichmentResultEntity?

   @Query(
      """
        DELETE FROM enrichment_results
        WHERE id NOT IN (SELECT id FROM enrichment_results ORDER BY fecha DESC LIMIT :keepLast)
    """
   )
   suspend fun clearEnrichmentHistory(keepLast: Int)

   // ═══════════════════════════════════════════════════════════════
   // ACCUMULATED STATISTICS
   // ═══════════════════════════════════════════════════════════════

   @Query("SELECT COUNT(*) FROM scan_results") suspend fun getTotalScans(): Int

   @Query("SELECT COALESCE(SUM(cancionesNuevas + cancionesActualizadas), 0) FROM scan_results")
   suspend fun getTotalScannedSongs(): Int

   @Query("SELECT COALESCE(SUM(cancionesLimpiadas), 0) FROM cleaning_results")
   suspend fun getTotalCleanedSongs(): Int

   @Query("SELECT COUNT(*) FROM enrichment_results WHERE exitoso = 1")
   suspend fun getTotalEnrichedSongs(): Int

   @Query("SELECT COUNT(*) FROM enrichment_results WHERE letraEncontrada = 1")
   suspend fun getTotalLyricsObtained(): Int

   // ═══════════════════════════════════════════════════════════════
   // PROCESSING TIME
   // ═══════════════════════════════════════════════════════════════

   @Query("SELECT COALESCE(SUM(tiempoMs), 0) FROM scan_results")
   suspend fun getScanProcessingTime(): Long

   @Query("SELECT COALESCE(SUM(tiempoMs), 0) FROM cleaning_results")
   suspend fun getCleaningProcessingTime(): Long

   @Query(
      """
        SELECT COALESCE(
            (SELECT SUM(tiempoMs) FROM scan_results) +
            (SELECT SUM(tiempoMs) FROM cleaning_results), 0
        )
    """
   )
   suspend fun getTotalProcessingTime(): Long
}
