package com.codewithmisu.fitatlas.exercise.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ExerciseRemoteSource {

    @GET("/api/v1/exercises/search")
    suspend fun fetchExercises(@Query("search") query: String): ExerciseResponseDTO

    @GET("/api/v1/exercises/{exerciseId}")
    suspend fun fetchExerciseDetail(@Path("exerciseId") exerciseId: String): ExerciseDetailResponseDTO
}


