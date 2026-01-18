package com.pmk.freeplayer.domain.model.enums

enum class FuenteLetra(val nombreMostrar: String) {
	INCRUSTADA("Incrustada en archivo"),
	ARCHIVO_LOCAL("Archivo .lrc local"),
	EN_LINEA("Descargada de internet"),
	MANUAL("Ingresada manualmente"),
}