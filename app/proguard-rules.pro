# ═══════════════════════════════════════════════════════════════════════════════
# PROGUARD RULES - Music Player App
# ═══════════════════════════════════════════════════════════════════════════════

# ─────────────────────────────────────────────────────────────────────────────
# REGLAS GENERALES
# ─────────────────────────────────────────────────────────────────────────────
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Mantener nombres de clases en stack traces
-renamesourcefileattribute SourceFile

# ─────────────────────────────────────────────────────────────────────────────
# KOTLIN
# ─────────────────────────────────────────────────────────────────────────────
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ─────────────────────────────────────────────────────────────────────────────
# MEDIA3 / EXOPLAYER - Esencial para reproducción de música
# ─────────────────────────────────────────────────────────────────────────────
-keep class androidx.media3.** { *; }
-keep interface androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Extensiones de Media3
-keep class androidx.media3.exoplayer.** { *; }
-keep class androidx.media3.session.** { *; }
-keep class androidx.media3.ui.** { *; }

# MediaSession service
-keep class * extends androidx.media3.session.MediaSessionService { *; }
-keep class * extends androidx.media3.session.MediaLibraryService { *; }

# ─────────────────────────────────────────────────────────────────────────────
# ROOM DATABASE
# ─────────────────────────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Mantener DAOs
-keep interface * extends androidx.room.RoomDatabase { *; }
-keep class * implements androidx.room.RoomDatabase { *; }

# ─────────────────────────────────────────────────────────────────────────────
# HILT / DAGGER
# ─────────────────────────────────────────────────────────────────────────────
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel

# ─────────────────────────────────────────────────────────────────────────────
# RETROFIT & OKHTTP
# ─────────────────────────────────────────────────────────────────────────────
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ─────────────────────────────────────────────────────────────────────────────
# MOSHI
# ─────────────────────────────────────────────────────────────────────────────
-keep class com.squareup.moshi.** { *; }
-keep interface com.squareup.moshi.** { *; }
-keepclassmembers class * {
    @com.squareup.moshi.* <methods>;
    @com.squareup.moshi.* <fields>;
}

# Mantener clases de datos que Moshi serializa
# Cambia el paquete por el tuyo
-keep class com.tuapp.musicplayer.data.model.** { *; }
-keep class com.tuapp.musicplayer.domain.model.** { *; }

# ─────────────────────────────────────────────────────────────────────────────
# GSON (si lo usas además de Moshi)
# ─────────────────────────────────────────────────────────────────────────────
-keep class com.google.gson.** { *; }
-keepattributes *Annotation*
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ─────────────────────────────────────────────────────────────────────────────
# FIREBASE
# ─────────────────────────────────────────────────────────────────────────────
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# ─────────────────────────────────────────────────────────────────────────────
# COIL (Carga de imágenes / carátulas)
# ─────────────────────────────────────────────────────────────────────────────
-keep class coil.** { *; }
-dontwarn coil.**

# ─────────────────────────────────────────────────────────────────────────────
# JSOUP (Si extraes letras de canciones)
# ─────────────────────────────────────────────────────────────────────────────
-keep class org.jsoup.** { *; }
-dontwarn org.jsoup.**

# ─────────────────────────────────────────────────────────────────────────────
# COMPOSE
# ─────────────────────────────────────────────────────────────────────────────
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ─────────────────────────────────────────────────────────────────────────────
# DATASTORE
# ─────────────────────────────────────────────────────────────────────────────
-keep class androidx.datastore.** { *; }

# ─────────────────────────────────────────────────────────────────────────────
# TUS MODELOS DE DATOS - IMPORTANTE: Actualiza con tus paquetes
# ─────────────────────────────────────────────────────────────────────────────
# Ejemplo: mantener entidades de Room y modelos de red
# -keep class com.tuapp.musicplayer.data.local.entity.** { *; }
# -keep class com.tuapp.musicplayer.data.remote.dto.** { *; }

# ─────────────────────────────────────────────────────────────────────────────
# SERVICIOS DE MÚSICA - Mantener servicios en segundo plano
# ─────────────────────────────────────────────────────────────────────────────
# -keep class com.tuapp.musicplayer.service.** { *; }

# ─────────────────────────────────────────────────────────────────────────────
# ENUMS
# ─────────────────────────────────────────────────────────────────────────────
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ─────────────────────────────────────────────────────────────────────────────
# PARCELABLES
# ─────────────────────────────────────────────────────────────────────────────
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# ─────────────────────────────────────────────────────────────────────────────
# SERIALIZABLE
# ─────────────────────────────────────────────────────────────────────────────
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}