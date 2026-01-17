package com.pmk.freeplayer.domain.model.enums

enum class EstadoCancion {
	CRUDO,        // Metadatos extraídos sin procesar
	LIMPIO,       // Metadatos procesados y validados
	ENRIQUECIDO   // Complementado con Genius + letra
}