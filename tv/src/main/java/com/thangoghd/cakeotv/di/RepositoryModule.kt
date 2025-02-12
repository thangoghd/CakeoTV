package com.thangoghd.cakeotv.di

import android.content.Context
import com.thangoghd.cakeotv.data.repository.MatchRepository
import com.thangoghd.cakeotv.data.repository.MatchRepositoryImpl
import com.thangoghd.cakeotv.data.repository.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePreferencesRepository(
        @ApplicationContext context: Context
    ): PreferencesRepository {
        return PreferencesRepository(context)
    }

    @Provides
    @Singleton
    fun provideMatchRepository(
        matchRepositoryImpl: MatchRepositoryImpl
    ): MatchRepository {
        return matchRepositoryImpl
    }
}
