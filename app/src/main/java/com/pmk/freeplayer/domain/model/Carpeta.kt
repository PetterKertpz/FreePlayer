package com.pmk.freeplayer.domain.model

data class Carpeta(
    val ruta: String,
    val nombre: String,
    val cantidadCanciones: Int,
    val tamanioTotal: Long,                 // bytes
    val estaOculta: Boolean = false         // excluida por el usuario
) {
    val tamanioFormateado: String
        get() {
            val mb = tamanioTotal / (1024.0 * 1024.0)
            return if (mb >= 1024) {
                "%.1f GB".format(mb / 1024)
            } else {
                "%.1f MB".format(mb)
            }
        }
}