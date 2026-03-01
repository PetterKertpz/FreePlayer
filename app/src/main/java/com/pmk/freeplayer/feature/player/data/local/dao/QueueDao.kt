package com.pmk.freeplayer.feature.player.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pmk.freeplayer.feature.player.data.local.entity.QueueEntity
import com.pmk.freeplayer.feature.player.data.local.relation.QueueItemSong
import kotlinx.coroutines.flow.Flow

@Dao
interface QueueDao {
	
	// ==================== LECTURA (UI) ====================
	
	/**
	 * 🚀 OBTENER COLA (Reactiva)
	 * Devuelve la lista unida con los datos de la canción (Título, Artista, Portada).
	 * Si cambias el orden, la UI se actualiza sola.
	 */
	@Transaction
	@Query("SELECT * FROM queue ORDER BY sort_order ASC")
	fun getQueue(): Flow<List<QueueItemSong>>
	
	/**
	 * Versión síncrona para el Player (Service)
	 */
	@Transaction
	@Query("SELECT * FROM queue ORDER BY sort_order ASC")
	suspend fun getQueueSync(): List<QueueItemSong>
	
	@Query("SELECT COUNT(*) FROM queue")
	suspend fun getQueueSize(): Int
	
	// ==================== ESCRITURA BÁSICA ====================
	
	@Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
	suspend fun insert(item: QueueEntity): Long
	
	/**
	 * Reemplazo masivo optimizado (Play Album / Playlist).
	 * Usa esto en lugar de insertar uno por uno.
	 */
	@Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
	suspend fun insertAll(items: List<QueueEntity>)
	
	// ==================== ACTUALIZACIÓN (Drag & Drop) ====================
	
	/**
	 * Actualiza solo el orden de un item.
	 * Útil para Drag & Drop rápido.
	 */
	@Query("UPDATE queue SET sort_order = :newOrder WHERE id = :id")
	suspend fun updateOrder(id: Long, newOrder: Int)
	
	/**
	 * Actualiza una lista completa (útil para reordenamientos complejos o shuffle persistente).
	 */
	@Update
	suspend fun updateAll(items: List<QueueEntity>)
	
	// ==================== ELIMINACIÓN ====================
	
	/**
	 * Borra un item específico (Swipe to dismiss).
	 */
	@Query("DELETE FROM queue WHERE id = :id")
	suspend fun deleteById(id: Long)
	
	@Query("DELETE FROM queue WHERE song_id = :songId")
	suspend fun deleteBySongId(songId: Long)
	
	/**
	 * 🧹 LIMPIEZA TOTAL
	 * Se llama antes de cargar una nueva lista (ej: click en un nuevo álbum).
	 */
	@Query("DELETE FROM queue")
	suspend fun clearQueue()
	
	// ==================== OPERACIONES TRANSACCIONALES (Lógica de Negocio) ====================
	
	/**
	 * Reemplaza la cola actual con una nueva lista de canciones.
	 * Operación atómica: Borra y Escribe en un solo paso.
	 */
	@Transaction
	suspend fun replaceQueue(songs: List<QueueEntity>) {
		clearQueue()
		insertAll(songs)
	}
	
	/**
	 * Agrega una canción al final de la cola.
	 * Calcula automáticamente el nuevo sort_order.
	 */
	@Transaction
	suspend fun addToEnd(songId: Long) {
		val maxOrder = getMaxSortOrder() ?: -1
		val newItem = QueueEntity(
			songId = songId,
			sortOrder = maxOrder + 1
		)
		insert(newItem)
	}
	
	/**
	 * Agrega una canción para reproducir "A continuación" (Justo después del actual).
	 * Empuja el resto de la cola hacia abajo.
	 */
	@Transaction
	suspend fun playNext(songId: Long, currentSortOrder: Int) {
		// 1. Desplazar todos los items posteriores +1
		shiftOrdersUp(fromOrder = currentSortOrder + 1)
		
		// 2. Insertar el nuevo item en el hueco
		val newItem = QueueEntity(
			songId = songId,
			sortOrder = currentSortOrder + 1
		)
		insert(newItem)
	}
	
	// ==================== UTILIDADES INTERNAS ====================
	
	@Query("SELECT MAX(sort_order) FROM queue")
	suspend fun getMaxSortOrder(): Int?
	
	@Query("UPDATE queue SET sort_order = sort_order + 1 WHERE sort_order >= :fromOrder")
	suspend fun shiftOrdersUp(fromOrder: Int)
}