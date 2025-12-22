package com.codewithmisu.fitatlas.body.data

import com.codewithmisu.fitatlas.body.domain.BodyParts
import com.codewithmisu.fitatlas.body.domain.BodyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BodyRepositoryImpl @Inject constructor(
    private val bodyRemoteSource: BodyRemoteSource,
    private val bodyLocalSource: BodyLocalSource
) : BodyRepository {

    override suspend fun getBodyParts(forceRefresh: Boolean): List<BodyParts> {
        val bodyEntities = bodyLocalSource.getAll() as MutableList
        if (bodyEntities.isEmpty() || forceRefresh) {
            val response = bodyRemoteSource.fetchBodyParts()
            val bodyList = response.data
            val entities = bodyList.map {
                BodyEntity(name = it.name, imageUrl = it.imageUrl)
            }
            bodyLocalSource.saveAll(entities)

            // Read again (because of id - auto increment)
            val list = bodyLocalSource.getAll()
            bodyEntities.clear()
            bodyEntities.addAll(list)
        }
        return bodyEntities.map { it.toDomain() }
    }

    override fun watchBodyParts(): Flow<List<BodyParts>> {
        return bodyLocalSource.watchAll().map {
            it.map { entity ->
                entity.toDomain()
            }
        }
    }
}