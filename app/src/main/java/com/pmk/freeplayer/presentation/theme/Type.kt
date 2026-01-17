package com.pmk.freeplayer.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pmk.FreePlayer.R

// ─────────────────────────────────────────────────────────────────
// 🔤 FAMILIAS DE FUENTES
// ─────────────────────────────────────────────────────────────────

// Fuente principal (para títulos y énfasis)
// Sugerencia: Poppins, Montserrat, o Inter
val FuentePrincipal = FontFamily(
    Font(R.font.poppins_light, FontWeight.Light),
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

// Fuente secundaria (para cuerpo de texto)
// Sugerencia: Inter, Roboto, o Nunito
val FuenteSecundaria = FontFamily(
    Font(R.font.nunito_light, FontWeight.Light),
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_medium, FontWeight.Medium),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_bold, FontWeight.Bold)
)

// Fuente monoespaciada (para tiempos, contadores)
val FuenteMono = FontFamily(
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
    Font(R.font.jetbrains_mono_medium, FontWeight.Medium)
)

// ─────────────────────────────────────────────────────────────────
// 📐 ESCALA TIPOGRÁFICA MATERIAL 3
// ─────────────────────────────────────────────────────────────────
val Tipografia = Typography(
    // ═══════════════════════════════════════════════════════════
    // DISPLAY - Títulos muy grandes (pantalla de reproducción)
    // ═══════════════════════════════════════════════════════════
    displayLarge = TextStyle(
        fontFamily = FuentePrincipal,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FuentePrincipal,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FuentePrincipal,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),

    // ═══════════════════════════════════════════════════════════
    // HEADLINE - Encabezados de sección
    // ═══════════════════════════════════════════════════════════
    headlineLarge = TextStyle(
        fontFamily = FuentePrincipal,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FuentePrincipal,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FuentePrincipal,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    // ═══════════════════════════════════════════════════════════
    // TITLE - Títulos de tarjetas, canciones, álbumes
    // ═══════════════════════════════════════════════════════════
    titleLarge = TextStyle(
        fontFamily = FuentePrincipal,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FuentePrincipal,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FuentePrincipal,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // ═══════════════════════════════════════════════════════════
    // BODY - Texto de cuerpo, descripciones
    // ═══════════════════════════════════════════════════════════
    bodyLarge = TextStyle(
        fontFamily = FuenteSecundaria,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FuenteSecundaria,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FuenteSecundaria,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // ═══════════════════════════════════════════════════════════
    // LABEL - Etiquetas, botones, chips
    // ═══════════════════════════════════════════════════════════
    labelLarge = TextStyle(
        fontFamily = FuenteSecundaria,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FuenteSecundaria,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FuenteSecundaria,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// ─────────────────────────────────────────────────────────────────
// 🎵 ESTILOS ESPECÍFICOS DEL REPRODUCTOR
// ─────────────────────────────────────────────────────────────────
object EstilosReproductor {
    // Título de canción en pantalla de reproducción
    val tituloCancionGrande = TextStyle(
        fontFamily = FuentePrincipal,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    )

    // Artista en pantalla de reproducción
    val artistaGrande = TextStyle(
        fontFamily = FuenteSecundaria,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    )

    // Título de canción en lista
    val tituloCancionLista = TextStyle(
        fontFamily = FuentePrincipal,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    )

    // Subtítulo (artista - álbum) en lista
    val subtituloLista = TextStyle(
        fontFamily = FuenteSecundaria,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp,
    )

    // Tiempo de reproducción (03:45)
    val tiempoReproduccion = TextStyle(
        fontFamily = FuenteMono,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    )

    // Duración pequeña en listas
    val duracionPequenia = TextStyle(
        fontFamily = FuenteMono,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    )

    // Letra de canción
    val letraCancion = TextStyle(
        fontFamily = FuenteSecundaria,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.25.sp
    )

    // Letra de canción resaltada (línea actual)
    val letraCancionActiva = TextStyle(
        fontFamily = FuenteSecundaria,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.25.sp
    )

    // Contador de canciones, estadísticas
    val estadistica = TextStyle(
        fontFamily = FuenteMono,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    )
}