package com.codewithmisu.fitatlas.exercise

import com.codewithmisu.fitatlas.ApiClient
import com.codewithmisu.fitatlas.AppDatabase
import com.codewithmisu.fitatlas.exercise.data.ExerciseLocalSource
import com.codewithmisu.fitatlas.exercise.data.ExerciseRemoteSource
import com.codewithmisu.fitatlas.exercise.data.ExerciseRepositoryImpl
import com.codewithmisu.fitatlas.exercise.domain.ExerciseRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ExerciseModule {

    companion object {

        @Provides
        fun providesExerciseLocalSource(appDatabase: AppDatabase): ExerciseLocalSource {
            return appDatabase.exerciseLocalSource()
        }

        @Provides
        fun providesExerciseRemoteSource(apiClient: ApiClient): ExerciseRemoteSource {
            return apiClient.create(ExerciseRemoteSource::class.java)
        }
    }

    @Binds
    abstract fun providesExerciseRepository(
        repositoryImpl: ExerciseRepositoryImpl
    ): ExerciseRepository
}