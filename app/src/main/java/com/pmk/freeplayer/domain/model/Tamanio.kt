package com.pmk.freeplayer.domain.model

@JvmInline
value class Tamanio(val bytes: Long) {
	
	val enKB: Double get() = bytes / 1024.0
	val enMB: Double get() = enKB / 1024.0
	val enGB: Double get() = enMB / 1024.0
	
	fun formateado(): String {
		return when {
			enGB >= 1 -> "%.1f GB".format(enGB)
			enMB >= 1 -> "%.1f MB".format(enMB)
			enKB >= 1 -> "%.1f KB".format(enKB)
			else -> "$bytes B"
		}
	}
	
	operator fun plus(otro: Tamanio) = Tamanio(bytes + otro.bytes)
	
	companion object {
		val CERO = Tamanio(0)
		fun desdeMB(mb: Double) = Tamanio((mb * 1024 * 1024).toLong())
	}
}