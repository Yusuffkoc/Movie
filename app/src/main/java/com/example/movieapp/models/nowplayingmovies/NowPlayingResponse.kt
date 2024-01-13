package com.example.movieapp.models.nowplayingmovies

import android.os.Parcel
import android.os.Parcelable
import com.example.movieapp.models.upcomingmovies.Result
import com.example.movieapp.models.moviedetail.Dates

data class NowPlayingResponse(
    val dates: Dates,
    val page: Int,
    val results: MutableList<Result> = arrayListOf(),
    val total_pages: Int,
    val total_results: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        TODO("dates"),
        parcel.readInt(),
        TODO("results"),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(page)
        parcel.writeInt(total_pages)
        parcel.writeInt(total_results)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NowPlayingResponse> {
        override fun createFromParcel(parcel: Parcel): NowPlayingResponse {
            return NowPlayingResponse(parcel)
        }

        override fun newArray(size: Int): Array<NowPlayingResponse?> {
            return arrayOfNulls(size)
        }
    }
}