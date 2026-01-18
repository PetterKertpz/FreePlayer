package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.enums.ModoRepeticion

data class EstadoReproductorGuardado(
	val cancionId: Long?,
	val posicion: Long,
	val cola: ColaReproduccion,
	val modoRepeticion: ModoRepeticion,
	val aleatorioActivado: Boolean
)