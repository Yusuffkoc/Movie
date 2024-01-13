package com.example.movieapp.repository

import com.example.movieapp.models.moviedetail.MovieDetailResponse
import com.example.movieapp.models.nowplayingmovies.NowPlayingResponse
import com.example.movieapp.models.popularmovies.PopularMovieResponse
import com.example.movieapp.models.upcomingmovies.UpComingMoviesResponse
import retrofit2.Response

interface MovieRepository {
    suspend fun getPopularMovies(language: String, pageNumber: Int): Response<PopularMovieResponse>
    suspend fun getNowPlayingMovies(language: String, pageNumber: Int): Response<NowPlayingResponse>
    suspend fun getUpComingMovies(language: String, pageNumber: Int): Response<UpComingMoviesResponse>
    suspend fun getMovieDetails(id:Int):Response<MovieDetailResponse>

}
