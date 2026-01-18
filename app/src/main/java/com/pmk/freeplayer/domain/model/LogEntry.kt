package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.enums.FaseProceso
import com.pmk.freeplayer.domain.model.enums.NivelLog

data class LogEntry(
    val id: Long = 0,
    val timestamp: Long,
    val nivel: NivelLog,
    val fase: FaseProceso,
    val mensaje: String,
    val cancionId: Long?,
    val detalles: Map<String, String>?,
    val stackTrace: String?,
)
