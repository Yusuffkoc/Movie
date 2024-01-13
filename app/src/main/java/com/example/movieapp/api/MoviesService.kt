package com.example.movieapp.api

import com.example.movieapp.models.moviedetail.MovieDetailResponse
import com.example.movieapp.models.nowplayingmovies.NowPlayingResponse
import com.example.movieapp.models.popularmovies.PopularMovieResponse
import com.example.movieapp.models.upcomingmovies.UpComingMoviesResponse
import com.example.movieapp.util.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesService {
    @GET("/3/movie/popular")
    suspend fun getPopularMovies(
        @Query("language")
        language: String = "en-US",
        @Query("page")
        page: Int = 1,
        @Header("Authorization")
        token: String = API_KEY
    ): Response<PopularMovieResponse>

    @GET("/3/movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("language")
        language: String = "en-US",
        @Query("page")
        page: Int = 1,
        @Header("Authorization")
        token: String = API_KEY
    ): Response<NowPlayingResponse>

    @GET("/3/movie/upcoming")
    suspend fun getUpComingMovies(
        @Query("language")
        language: String = "en-US",
        @Query("page")
        page: Int = 1,
        @Header("Authorization")
        token: String = API_KEY
    ): Response<UpComingMoviesResponse>

    @GET("/3/movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id")
        movieId: Int,
        @Header("Authorization")
        token: String = API_KEY
    ): Response<MovieDetailResponse>

}