package com.example.movieapp.ui.fragment

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.ContactsContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.movieapp.models.moviedetail.MovieDetailResponse
import com.example.movieapp.models.nowplayingmovies.NowPlayingResponse
import com.example.movieapp.repository.MovieRepositoryImpl
import com.example.movieapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    app: Application,
    private val repository: MovieRepositoryImpl
) : AndroidViewModel(app) {

    val moviesResponse: MutableLiveData<Resource<MovieDetailResponse>> = MutableLiveData()

    fun getMovies(movieId: Int) = viewModelScope.launch {
        safeGetMoviesCall(movieId)
    }

    private suspend fun safeGetMoviesCall(movieId: Int) {
        try {
            if (hasInternetConnection()) {
                val response = repository.getMovieDetails(movieId)
                moviesResponse.postValue(handleMoviesResponse(response))
            } else {
                moviesResponse.postValue(Resource.Error("No Internet Connection"))
            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> moviesResponse.postValue(Resource.Error("Network Failure"))
                else -> moviesResponse.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleMoviesResponse(response: Response<MovieDetailResponse>): Resource<MovieDetailResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<android.app.Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}

