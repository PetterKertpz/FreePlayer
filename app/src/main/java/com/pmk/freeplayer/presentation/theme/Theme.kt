package com.pmk.freeplayer.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ─────────────────────────────────────────────────────────────────
// 🌙 ESQUEMA DE COLORES OSCURO (Windsor)
// ─────────────────────────────────────────────────────────────────
private val EsquemaOscuro = darkColorScheme(
    // Colores primarios
    primary = PaletaWindsor.windsor400,
    onPrimary = ColoresNeutros.blanco,
    primaryContainer = PaletaWindsor.windsor700,
    onPrimaryContainer = PaletaWindsor.windsor100,

    // Colores secundarios
    secondary = PaletaWindsor.windsor300,
    onSecondary = PaletaWindsor.windsor900,
    secondaryContainer = PaletaWindsor.windsor800,
    onSecondaryContainer = PaletaWindsor.windsor200,

    // Colores terciarios
    tertiary = PaletaMagenta.magenta400,
    onTertiary = PaletaMagenta.magenta950,
    tertiaryContainer = PaletaMagenta.magenta800,
    onTertiaryContainer = PaletaMagenta.magenta200,

    // Fondos
    background = PaletaWindsor.windsor950,
    onBackground = ColoresNeutros.gris100,

    // Superficies
    surface = PaletaWindsor.windsor900,
    onSurface = ColoresNeutros.gris100,
    surfaceVariant = PaletaWindsor.windsor800,
    onSurfaceVariant = ColoresNeutros.gris300,
    surfaceTint = PaletaWindsor.windsor400,

    // Inversas
    inverseSurface = ColoresNeutros.gris100,
    inverseOnSurface = PaletaWindsor.windsor900,
    inversePrimary = PaletaWindsor.windsor600,

    // Contornos
    outline = PaletaWindsor.windsor600,
    outlineVariant = PaletaWindsor.windsor700,

    // Estados
    error = ColoresEstado.error,
    onError = ColoresNeutros.blanco,
    errorContainer = ColoresEstado.errorOscuro,
    onErrorContainer = ColoresEstado.errorClaro,

    // Scrim (overlay)
    scrim = ColoresNeutros.negro
)

// ─────────────────────────────────────────────────────────────────
// ☀️ ESQUEMA DE COLORES CLARO (Magenta Fuchsia)
// ─────────────────────────────────────────────────────────────────
private val EsquemaClaro = lightColorScheme(
    // Colores primarios
    primary = PaletaMagenta.magenta700,
    onPrimary = ColoresNeutros.blanco,
    primaryContainer = PaletaMagenta.magenta200,
    onPrimaryContainer = PaletaMagenta.magenta900,

    // Colores secundarios
    secondary = PaletaMagenta.magenta600,
    onSecondary = ColoresNeutros.blanco,
    secondaryContainer = PaletaMagenta.magenta100,
    onSecondaryContainer = PaletaMagenta.magenta900,

    // Colores terciarios
    tertiary = PaletaWindsor.windsor500,
    onTertiary = ColoresNeutros.blanco,
    tertiaryContainer = PaletaWindsor.windsor100,
    onTertiaryContainer = PaletaWindsor.windsor900,

    // Fondos
    background = PaletaMagenta.magenta50,
    onBackground = PaletaMagenta.magenta950,

    // Superficies
    surface = PaletaMagenta.magenta50,
    onSurface = PaletaMagenta.magenta950,
    surfaceVariant = PaletaMagenta.magenta100,
    onSurfaceVariant = PaletaMagenta.magenta800,
    surfaceTint = PaletaMagenta.magenta700,

    // Inversas
    inverseSurface = PaletaMagenta.magenta900,
    inverseOnSurface = PaletaMagenta.magenta100,
    inversePrimary = PaletaMagenta.magenta300,

    // Contornos
    outline = PaletaMagenta.magenta600,
    outlineVariant = PaletaMagenta.magenta300,

    // Estados
    error = ColoresEstado.error,
    onError = ColoresNeutros.blanco,
    errorContainer = ColoresEstado.errorClaro,
    onErrorContainer = ColoresEstado.errorOscuro,

    // Scrim
    scrim = ColoresNeutros.negro
)

// ─────────────────────────────────────────────────────────────────
// 🎨 TEMA PRINCIPAL DE LA APLICACIÓN
// ─────────────────────────────────────────────────────────────────
@Composable
fun MusicPlayerTheme(
    modoOscuro: Boolean = isSystemInDarkTheme(),
    usarColoresDinamicos: Boolean = false,  // Material You
    contenido: @Composable () -> Unit
) {
    val esquemaColores = when {
        // Colores dinámicos (Android 12+)
        usarColoresDinamicos && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val contexto = LocalContext.current
            if (modoOscuro) {
                dynamicDarkColorScheme(contexto)
            } else {
                dynamicLightColorScheme(contexto)
            }
        }
        // Tema oscuro personalizado
        modoOscuro -> EsquemaOscuro
        // Tema claro personalizado
        else -> EsquemaClaro
    }

    // Configurar barra de estado
    val vista = LocalView.current
    if (!vista.isInEditMode) {
        SideEffect {
            val ventana = (vista.context as Activity).window
            ventana.statusBarColor = esquemaColores.background.toArgb()
            ventana.navigationBarColor = esquemaColores.background.toArgb()
            WindowCompat.getInsetsController(ventana, vista).apply {
                isAppearanceLightStatusBars = !modoOscuro
                isAppearanceLightNavigationBars = !modoOscuro
            }
        }
    }

    MaterialTheme(
        colorScheme = esquemaColores,
        typography = Tipografia,
        shapes = Formas,
        content = contenido
    )
}

// ─────────────────────────────────────────────────────────────────
// 🎵 EXTENSIONES PARA COLORES DEL REPRODUCTOR
// ─────────────────────────────────────────────────────────────────
object ColoresTema {
    @Composable
    fun favorito() = ColoresReproductor.favorito

    @Composable
    fun favoritoInactivo() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun reproduciendo() = ColoresReproductor.reproduciendo

    @Composable
    fun shuffle() = ColoresReproductor.shuffle

    @Composable
    fun repetir() = ColoresReproductor.repetir

    @Composable
    fun ecualizador() = ColoresReproductor.ecualizador

    @Composable
    fun textoSecundario() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun divisor() = MaterialTheme.colorScheme.outlineVariant

    @Composable
    fun superficieElevada() = MaterialTheme.colorScheme.surfaceVariant
}