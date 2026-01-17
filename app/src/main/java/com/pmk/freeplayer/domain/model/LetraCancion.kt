package com.pmk.freeplayer.domain.model

data class LetraCancion(
    val cancionId: Long,
    val tipo: TipoLetra,
    val contenido: String?,                 // letra completa (texto plano)
    val lineasSincronizadas: List<LineaLetra>?,
    val fuente: FuenteLetra,
    val idioma: String?                     // código ISO (es, en, etc.)
) {
    val tieneLetra: Boolean
        get() = tipo != TipoLetra.NINGUNA

    val estaSincronizada: Boolean
        get() = tipo == TipoLetra.SINCRONIZADA && !lineasSincronizadas.isNullOrEmpty()
}

data class LineaLetra(
    val tiempoInicio: Long,                 // milisegundos desde inicio
    val texto: String,
    val traduccion: String? = null
) {
    // Tiempo formateado (ej: "01:23")
    val tiempoFormateado: String
        get() {
            val minutos = (tiempoInicio / 1000) / 60
            val segundos = (tiempoInicio / 1000) % 60
            return "${minutos.toString().padStart(2, '0')}:${segundos.toString().padStart(2, '0')}"
        }
}

enum class TipoLetra {
    NINGUNA,            // sin letra disponible
    SIMPLE,             // texto sin sincronizar
    SINCRONIZADA        // formato LRC con tiempos
}

enum class FuenteLetra(val nombreMostrar: String) {
    INCRUSTADA("Incrustada en archivo"),
    ARCHIVO_LOCAL("Archivo .lrc local"),
    EN_LINEA("Descargada de internet"),
    MANUAL("Ingresada manualmente")
}