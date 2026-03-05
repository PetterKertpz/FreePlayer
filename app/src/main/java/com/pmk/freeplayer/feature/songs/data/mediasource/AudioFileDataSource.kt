package com.pmk.freeplayer.feature.songs.data.mediasource

import com.pmk.freeplayer.feature.songs.domain.model.Song

/**
 * Contrato para operaciones sobre archivos de audio del dispositivo.
 *
 * Abstrae el acceso a MediaStore y ContentResolver de Android,
 * manteniendo [SongRepositoryImpl] libre de dependencias de Android.
 *
 * La implementación vive en [AudioFileDataSourceImpl].
 */
interface AudioFileDataSource {
	
	/**
	 * Escribe los metadatos del [Song] en el archivo físico usando AudioTagger.
	 * @return true si la escritura fue exitosa.
	 */
	suspend fun writeMetadataToFile(song: Song): Boolean
	
	/**
	 * Solicita la eliminación del archivo al sistema vía MediaStore/ContentResolver.
	 * En Android 10+ requiere [RecoverableSecurityException] manejado en la UI.
	 * @return true si el archivo fue eliminado exitosamente.
	 */
	suspend fun deleteFileFromDevice(filePath: String): Boolean
}