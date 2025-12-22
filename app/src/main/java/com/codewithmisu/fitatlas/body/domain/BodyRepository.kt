package com.codewithmisu.fitatlas.body.domain

import kotlinx.coroutines.flow.Flow

interface BodyRepository {

    suspend fun getBodyParts(forceRefresh: Boolean = false): List<BodyParts>

    fun watchBodyParts(): Flow<List<BodyParts>>
}