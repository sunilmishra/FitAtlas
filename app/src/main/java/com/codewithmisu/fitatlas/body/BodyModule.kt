package com.codewithmisu.fitatlas.body

import com.codewithmisu.fitatlas.ApiClient
import com.codewithmisu.fitatlas.AppDatabase
import com.codewithmisu.fitatlas.body.data.BodyLocalSource
import com.codewithmisu.fitatlas.body.data.BodyRemoteSource
import com.codewithmisu.fitatlas.body.data.BodyRepositoryImpl
import com.codewithmisu.fitatlas.body.domain.BodyRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

/**
 * Hilt module for providing dependencies related to Body feature.
 */
@Module
@InstallIn(ViewModelComponent::class)
abstract class BodyModule {

    companion object {
        @Provides
        fun providesBodyLocalSource(appDatabase: AppDatabase): BodyLocalSource {
            return appDatabase.bodyLocalSource()
        }

        @Provides
        fun providesBodyRemoteSource(apiClient: ApiClient): BodyRemoteSource {
            return apiClient.create(BodyRemoteSource::class.java)
        }
    }

    // Binds the implementation to the interface (compile-time efficient)
    @Binds
    abstract fun bindBodyRepository(repositoryImpl: BodyRepositoryImpl): BodyRepository
}
