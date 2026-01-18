package com.pmk.freeplayer.domain.model

@JvmInline
value class Duracion(val milisegundos: Long) {
	
	val enSegundos: Long get() = milisegundos / 1000
	val enMinutos: Long get() = enSegundos / 60
	val enHoras: Long get() = enMinutos / 60
	
	fun formatoCorto(): String {
		val min = enMinutos
		val seg = enSegundos % 60
		return "$min:${seg.toString().padStart(2, '0')}"
	}
	
	fun formatoLargo(): String {
		val h = enHoras
		val min = enMinutos % 60
		return if (h > 0) "${h}h ${min}min" else "$min min"
	}
	
	fun formatoPreciso(): String {
		val seg = enSegundos
		val min = enMinutos
		val h = enHoras
		return when {
			h > 0 -> "${h}h ${min % 60}m ${seg % 60}s"
			min > 0 -> "${min}m ${seg % 60}s"
			else -> "${seg}s"
		}
	}
	
	operator fun plus(otra: Duracion) = Duracion(milisegundos + otra.milisegundos)
	operator fun minus(otra: Duracion) = Duracion(milisegundos - otra.milisegundos)
	operator fun div(divisor: Long) = milisegundos / divisor
	operator fun rem(divisor: Long) = milisegundos % divisor
	operator fun compareTo(otra: Duracion) = milisegundos.compareTo(otra.milisegundos)
	
	companion object {
		val CERO = Duracion(0)
		fun desdeSegundos(seg: Long) = Duracion(seg * 1000)
		fun desdeMinutos(min: Long) = Duracion(min * 60 * 1000)
	}
}

// Extension para sumar duraciones
fun Iterable<Duracion>.sumar(): Duracion =
	Duracion(sumOf { it.milisegundos })

// Extension para entidades con duración
inline fun <T> Iterable<T>.sumarDuracion(selector: (T) -> Duracion): Duracion =
	Duracion(sumOf { selector(it).milisegundos })