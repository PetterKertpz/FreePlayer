package com.pmk.freeplayer.domain.model.audio

import java.util.Locale

@JvmInline
value class Genero(val valor: String) {
	
	val normalizado: String
		get() = valor.trim()
			.lowercase()
			.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
	
	companion object {
		val DESCONOCIDO = Genero("Desconocido")
		val OTRO = Genero("Otro")
	}
}