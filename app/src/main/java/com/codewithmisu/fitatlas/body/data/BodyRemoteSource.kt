package com.codewithmisu.fitatlas.body.data

import retrofit2.http.GET

interface BodyRemoteSource {

    @GET("/api/v1/bodyparts")
    suspend fun fetchBodyParts(): BodyPartResponseDTO
}