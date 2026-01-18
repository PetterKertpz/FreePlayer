package com.pmk.freeplayer.domain.model.enums

enum class TipoListaSistema(val nombreMostrar: String, val icono: String) {
	FAVORITOS("Favoritos", "heart"),
	REPRODUCIDAS_RECIENTEMENTE("Reproducidas recientemente", "history"),
	MAS_REPRODUCIDAS("Más reproducidas", "trending_up"),
	AGREGADAS_RECIENTEMENTE("Agregadas recientemente", "schedule")
}