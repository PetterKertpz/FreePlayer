package com.pmk.freeplayer.domain.model

@JvmInline
value class Duracion(val milisegundos: Long) {
	val enSegundos: Long get() = milisegundos / 1000
	val enMinutos: Long get() = enSegundos / 60
	val enHoras: Long get() = enMinutos / 60
	
	operator fun plus(otra: Duracion) = Duracion(milisegundos + otra.milisegundos)
	operator fun minus(otra: Duracion) = Duracion(milisegundos - otra.milisegundos)
	operator fun compareTo(otra: Duracion) = milisegundos.compareTo(otra.milisegundos)
	
	companion object {
		val CERO = Duracion(0)
	}
}

@JvmInline
value class Tamanio(val bytes: Long) {
	val enKB: Double get() = bytes / 1024.0
	val enMB: Double get() = enKB / 1024.0
	val enGB: Double get() = enMB / 1024.0
	
	operator fun plus(otro: Tamanio) = Tamanio(bytes + otro.bytes)
	
	companion object {
		val CERO = Tamanio(0)
	}
}

// Extensiones útiles para colecciones
fun Iterable<Duracion>.sumar(): Duracion = Duracion(sumOf { it.milisegundos })
inline fun <T> Iterable<T>.sumarDuracion(selector: (T) -> Duracion): Duracion = Duracion(sumOf { selector(it).milisegundos })