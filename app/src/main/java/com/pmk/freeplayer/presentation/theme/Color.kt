package com.pmk.freeplayer.presentation.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────
// 🌙 PALETA OSCURA - Windsor (Morado profundo)
// ─────────────────────────────────────────────────────────────────
object PaletaWindsor {
    val windsor50 = Color(0xFFF0E8FC)
    val windsor100 = Color(0xFFD7C2F7)
    val windsor200 = Color(0xFFBE9DF0)
    val windsor300 = Color(0xFFA478E8)
    val windsor400 = Color(0xFF8952DC)
    val windsor500 = Color(0xFF6519C3)
    val windsor600 = Color(0xFF480098)
    val windsor700 = Color(0xFF380075)
    val windsor800 = Color(0xFF2B0259)
    val windsor900 = Color(0xFF20093F)
    val windsor950 = Color(0xFF0F041F)
}

// ─────────────────────────────────────────────────────────────────
// ☀️ PALETA CLARA - Magenta Fuchsia
// ─────────────────────────────────────────────────────────────────
object PaletaMagenta {
    val magenta50 = Color(0xFFFFFFFF)
    val magenta100 = Color(0xFFFFD8FF)
    val magenta200 = Color(0xFFFFCBFF)
    val magenta300 = Color(0xFFFDBBFF)
    val magenta400 = Color(0xFFF9A6FF)
    val magenta500 = Color(0xFFF28BFF)
    val magenta600 = Color(0xFFDD6AFA)
    val magenta700 = Color(0xFFB157C8)
    val magenta800 = Color(0xFF834094)
    val magenta900 = Color(0xFF53265E)
    val magenta950 = Color(0xFF321A37)
}

// ─────────────────────────────────────────────────────────────────
// 🎨 COLORES SEMÁNTICOS (Neutros y estados)
// ─────────────────────────────────────────────────────────────────
object ColoresNeutros {
    // Blancos y grises claros
    val blanco = Color(0xFFFFFFFF)
    val gris50 = Color(0xFFFAFAFA)
    val gris100 = Color(0xFFF5F5F5)
    val gris200 = Color(0xFFEEEEEE)
    val gris300 = Color(0xFFE0E0E0)
    val gris400 = Color(0xFFBDBDBD)
    val gris500 = Color(0xFF9E9E9E)
    val gris600 = Color(0xFF757575)
    val gris700 = Color(0xFF616161)
    val gris800 = Color(0xFF424242)
    val gris900 = Color(0xFF212121)
    val negro = Color(0xFF000000)
}

object ColoresEstado {
    // Éxito
    val exito = Color(0xFF4CAF50)
    val exitoClaro = Color(0xFFC8E6C9)
    val exitoOscuro = Color(0xFF2E7D32)

    // Advertencia
    val advertencia = Color(0xFFFFC107)
    val advertenciaClaro = Color(0xFFFFF8E1)
    val advertenciaOscuro = Color(0xFFF57F17)

    // Error
    val error = Color(0xFFF44336)
    val errorClaro = Color(0xFFFFCDD2)
    val errorOscuro = Color(0xFFD32F2F)

    // Información
    val info = Color(0xFF2196F3)
    val infoClaro = Color(0xFFBBDEFB)
    val infoOscuro = Color(0xFF1976D2)
}

// ─────────────────────────────────────────────────────────────────
// 🎵 COLORES ESPECÍFICOS DEL REPRODUCTOR
// ─────────────────────────────────────────────────────────────────
object ColoresReproductor {
    val favorito = Color(0xFFE91E63)
    val favoritoInactivo = Color(0xFF9E9E9E)
    val reproduciendo = Color(0xFF4CAF50)
    val shuffle = Color(0xFF00BCD4)
    val repetir = Color(0xFFFF9800)
    val ecualizador = Color(0xFF9C27B0)
    val ondaSonido = Color(0xFF7C4DFF)
}
