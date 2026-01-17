package com.pmk.freeplayer.domain.model.enums

enum class ModoTema(val nombreMostrar: String) {
    CLARO("Claro"),
    OSCURO("Oscuro"),
    SISTEMA("Seguir sistema")
}

enum class ColorAcento(val nombreMostrar: String, val codigoHex: String) {
    PREDETERMINADO("Predeterminado", "#6200EE"),
    ROJO("Rojo", "#F44336"),
    ROSA("Rosa", "#E91E63"),
    MORADO("Morado", "#9C27B0"),
    AZUL("Azul", "#2196F3"),
    CIAN("Cian", "#00BCD4"),
    VERDE_AZULADO("Verde azulado", "#009688"),
    VERDE("Verde", "#4CAF50"),
    NARANJA("Naranja", "#FF9800"),
    AMBAR("Ámbar", "#FFC107")
}

enum class ModoRepeticion(val nombreMostrar: String) {
    DESACTIVADO("Desactivado"),
    UNA_CANCION("Repetir canción"),
    TODA_LA_LISTA("Repetir lista")
}

enum class PresetEcualizador(val nombreMostrar: String) {
    PLANO("Plano"),
    ROCK("Rock"),
    POP("Pop"),
    JAZZ("Jazz"),
    CLASICA("Clásica"),
    HIP_HOP("Hip Hop"),
    ELECTRONICA("Electrónica"),
    BASS_BOOST("Potenciador de graves"),
    TREBLE_BOOST("Potenciador de agudos"),
    VOCAL("Vocal"),
    PERSONALIZADO("Personalizado")
}

enum class TamanioFuente(val nombreMostrar: String, val escala: Float) {
    PEQUENIO("Pequeño", 0.85f),
    MEDIANO("Mediano", 1.0f),
    GRANDE("Grande", 1.2f),
    MUY_GRANDE("Muy grande", 1.4f)
}

enum class IdiomaApp(val codigo: String, val nombreMostrar: String) {
    SISTEMA("system", "Seguir sistema"),
    ESPANOL("es", "Español"),
    INGLES("en", "English"),
    PORTUGUES("pt", "Português"),
    FRANCES("fr", "Français"),
    ALEMAN("de", "Deutsch"),
    ITALIANO("it", "Italiano")
}