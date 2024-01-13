package com.example.movieapp.di

import com.example.movieapp.api.MoviesService
import com.example.movieapp.repository.MovieRepositoryImpl
import com.example.movieapp.repository.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
//    @Singleton
//    @Provides
//    fun provideMovieRepository(
//        movieService: MoviesService
//    ): MovieRepository {
//        return MovieRepositoryImpl(movieService)
//    }
}