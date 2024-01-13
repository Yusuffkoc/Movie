package com.example.movieapp.models.upcomingmovies

import com.example.movieapp.models.moviedetail.Dates

data class UpComingMoviesResponse(
    val dates: Dates,
    val page: Int,
    val results: MutableList<Result> = arrayListOf(),
    val total_pages: Int,
    val total_results: Int
)