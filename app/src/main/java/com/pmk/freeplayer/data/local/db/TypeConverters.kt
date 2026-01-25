package com.pmk.freeplayer.data.local.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 🔄 TYPE CONVERTERS
 * Solo para datos complejos que Room no puede aplanar automáticamente.
 */
class TypeConverters {
	
	private val gson = Gson()
	
	// ==================== LIST<STRING> <-> JSON ====================
	// Útil para guardar listas simples de IDs externos o tags
	@TypeConverter
	fun fromStringList(value: List<String>?): String? {
		return value?.let { gson.toJson(it) }
	}
	
	@TypeConverter
	fun toStringList(value: String?): List<String>? {
		return value?.let {
			val type = object : TypeToken<List<String>>() {}.type
			gson.fromJson(it, type)
		}
	}
	
	// ==================== MAP<STRING, STRING> <-> JSON ====================
	// Para el campo 'external_ids_json' (ej: {"spotify": "123", "youtube": "abc"})
	@TypeConverter
	fun fromStringMap(value: Map<String, String>?): String? {
		return value?.let { gson.toJson(it) }
	}
	
	@TypeConverter
	fun toStringMap(value: String?): Map<String, String>? {
		return value?.let {
			val type = object : TypeToken<Map<String, String>>() {}.type
			gson.fromJson(it, type)
		}
	}
}