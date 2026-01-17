package com.pmk.freeplayer.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Formas = Shapes(
    // Botones pequeños, chips
    extraSmall = RoundedCornerShape(4.dp),

    // Campos de texto, items de lista
    small = RoundedCornerShape(8.dp),

    // Tarjetas, diálogos pequeños
    medium = RoundedCornerShape(12.dp),

    // Bottom sheets, tarjetas grandes
    large = RoundedCornerShape(16.dp),

    // Contenedores principales
    extraLarge = RoundedCornerShape(24.dp)
)

// Formas específicas del reproductor
object FormasReproductor {
    // Portada del álbum
    val portadaAlbum = RoundedCornerShape(16.dp)
    val portadaAlbumPequenia = RoundedCornerShape(8.dp)

    // Mini reproductor (bottom bar)
    val miniReproductor = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)

    // Botones de control circular
    val botonControl = RoundedCornerShape(50)

    // Barra de progreso
    val barraProgreso = RoundedCornerShape(4.dp)

    // Slider thumb
    val sliderThumb = RoundedCornerShape(50)

    // Chip de género/etiqueta
    val chip = RoundedCornerShape(20.dp)

    // Tarjeta de playlist
    val tarjetaPlaylist = RoundedCornerShape(12.dp)

    // Bottom sheet de reproducción
    val bottomSheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

    // Ítem de lista con selección
    val itemLista = RoundedCornerShape(12.dp)
}