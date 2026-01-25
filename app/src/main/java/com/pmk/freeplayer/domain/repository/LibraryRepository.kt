package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.LibraryStats

interface LibraryRepository {
	
	suspend fun getLibraryStats(): LibraryStats
}