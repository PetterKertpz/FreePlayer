package com.pmk.freeplayer.domain.model.enums

enum class Genero(val nombreMostrar: String) {
    POP("Pop"),
    ROCK("Rock"),
    HIP_HOP("Hip Hop"),
    RAP("Rap"),
    RNB("R&B"),
    JAZZ("Jazz"),
    BLUES("Blues"),
    CLASICA("Clásica"),
    ELECTRONICA("Electrónica"),
    DANCE("Dance"),
    HOUSE("House"),
    TECHNO("Techno"),
    REGGAE("Reggae"),
    REGGAETON("Reggaetón"),
    LATINA("Latina"),
    SALSA("Salsa"),
    BACHATA("Bachata"),
    CUMBIA("Cumbia"),
    MERENGUE("Merengue"),
    VALLENATO("Vallenato"),
    RANCHERA("Ranchera"),
    MARIACHI("Mariachi"),
    BANDA("Banda"),
    NORTENA("Norteña"),
    COUNTRY("Country"),
    FOLK("Folk"),
    INDIE("Indie"),
    ALTERNATIVA("Alternativa"),
    METAL("Metal"),
    PUNK("Punk"),
    SOUL("Soul"),
    FUNK("Funk"),
    GOSPEL("Gospel"),
    BANDA_SONORA("Banda Sonora"),
    AMBIENTAL("Ambiental"),
    LOFI("Lo-Fi"),
    KPOP("K-Pop"),
    JPOP("J-Pop"),
    ANIME("Anime"),
    INSTRUMENTAL("Instrumental"),
    ACUSTICA("Acústica"),
    INFANTIL("Infantil"),
    VILLANCICOS("Villancicos"),
    PODCAST("Podcast"),
    AUDIOLIBRO("Audiolibro"),
    OTRO("Otro"),
    DESCONOCIDO("Desconocido");

    companion object {
        fun desdeTexto(valor: String?): Genero {
            if (valor.isNullOrBlank()) return DESCONOCIDO
            return entries.find {
                it.name.equals(valor, ignoreCase = true) ||
                        it.nombreMostrar.equals(valor, ignoreCase = true)
            } ?: OTRO
        }
    }
}