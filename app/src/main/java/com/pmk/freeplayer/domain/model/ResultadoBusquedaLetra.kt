package com.pmk.freeplayer.domain.model

sealed class ResultadoBusquedaLetra {
	data class Encontrada(val letra: LetraCancion) : ResultadoBusquedaLetra()
	data object NoEncontrada : ResultadoBusquedaLetra()
	data class Error(val mensaje: String, val causa: Throwable? = null) : ResultadoBusquedaLetra()
}
