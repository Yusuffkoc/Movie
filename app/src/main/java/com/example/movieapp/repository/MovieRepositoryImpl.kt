package com.example.movieapp.repository

import com.example.movieapp.api.MoviesService
import com.example.movieapp.models.moviedetail.MovieDetailResponse
import retrofit2.Response
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(private val moviesService: MoviesService) :
    MovieRepository {

    override suspend fun getPopularMovies(
        language: String,
        pageNumber: Int
    ) = moviesService.getPopularMovies(language = language, page = pageNumber)

    override suspend fun getNowPlayingMovies(
        language: String,
        pageNumber: Int
    ) = moviesService.getNowPlayingMovies(language = language, page = pageNumber)

    override suspend fun getUpComingMovies(
        language: String,
        pageNumber: Int
    ) = moviesService.getUpComingMovies(language = language, page = pageNumber)

    override suspend fun getMovieDetails(id: Int): Response<MovieDetailResponse> = moviesService.getMovieDetail(movieId = id)


}