package com.example.movieapp.models.popularmovies

import android.os.Parcel
import android.os.Parcelable
import com.example.movieapp.models.nowplayingmovies.Movie
import com.google.gson.annotations.SerializedName

class PopularMovieResponse(
    @SerializedName("page")
    var page: Int? = null,
    @SerializedName("results")
    var movies: MutableList<Movie> = arrayListOf(),
    @SerializedName("total_pages")
    var totalPages: Int,
    @SerializedName("total_results")
    var totalResults: Int? = null
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        TODO("movies"),
        parcel.readInt(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(page)
        parcel.writeInt(totalPages)
        parcel.writeValue(totalResults)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PopularMovieResponse> {
        override fun createFromParcel(parcel: Parcel): PopularMovieResponse {
            return PopularMovieResponse(parcel)
        }

        override fun newArray(size: Int): Array<PopularMovieResponse?> {
            return arrayOfNulls(size)
        }
    }
}