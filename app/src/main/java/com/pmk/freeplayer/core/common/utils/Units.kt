package com.pmk.freeplayer.core.common.utils

// --- Reemplaza a 'Duracion' ---
@JvmInline
value class TrackDuration(val millis: Long) : Comparable<TrackDuration> {
	
	val inSeconds: Long get() = millis / 1000
	val inMinutes: Long get() = inSeconds / 60
	val inHours: Long get() = inMinutes / 60
	
	// Helpers para UI (ej: para mostrar "3:05", usas inMinutes : secondsPart)
	val secondsPart: Long get() = inSeconds % 60
	val minutesPart: Long get() = inMinutes % 60
	
	operator fun plus(other: TrackDuration) = TrackDuration(millis + other.millis)
	
	// Protección contra negativos
	operator fun minus(other: TrackDuration) = TrackDuration((millis - other.millis).coerceAtLeast(0))
	
	override operator fun compareTo(other: TrackDuration) = millis.compareTo(other.millis)
	
	companion object {
		val ZERO = TrackDuration(0)
	}
}

// --- Reemplaza a 'Tamanio' ---
@JvmInline
value class FileSize(val bytes: Long) : Comparable<FileSize> {
	
	val inKB: Double get() = bytes / 1024.0
	val inMB: Double get() = inKB / 1024.0
	val inGB: Double get() = inMB / 1024.0
	
	operator fun plus(other: FileSize) = FileSize(bytes + other.bytes)
	operator fun minus(other: FileSize) = FileSize((bytes - other.bytes).coerceAtLeast(0))
	override operator fun compareTo(other: FileSize) = bytes.compareTo(other.bytes)
	
	companion object {
		val ZERO = FileSize(0)
	}
}

// --- Extensiones para Colecciones (Actualizadas al inglés) ---

fun Iterable<TrackDuration>.sum(): TrackDuration = TrackDuration(sumOf { it.millis })

inline fun <T> Iterable<T>.sumOfDuration(selector: (T) -> TrackDuration): TrackDuration =
	TrackDuration(sumOf { selector(it).millis })

fun Iterable<FileSize>.sum(): FileSize = FileSize(sumOf { it.bytes })