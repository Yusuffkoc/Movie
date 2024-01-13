package com.example.movieapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.databinding.NowPlayingMoviesRowBinding
import com.example.movieapp.models.upcomingmovies.Result
import com.example.movieapp.util.Constants.BASE_IMAGE_URL


class NowPlayingMoviesAdapter : RecyclerView.Adapter<NowPlayingMoviesAdapter.MoviesViewHolder>() {

    inner class MoviesViewHolder(private val binding: NowPlayingMoviesRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Result) {
            Glide.with(binding.root).load(BASE_IMAGE_URL + movie.poster_path).into(binding.movieId)
            binding.movieName.text = movie.original_title + "(" + parseYear(movie.release_date) + ")"
            binding.movieDetail.text = movie.overview
            //binding.movieDetail.text = parseYear(movie.release_date)

            binding.root.setOnClickListener {
                onItemClickListener?.let { it(movie) }
            }
        }
    }

    private fun parseYear(releaseDate: String?): CharSequence? {
        val date = releaseDate?.split("-")
        return date?.get(0)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Result>() {
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        return MoviesViewHolder(
            NowPlayingMoviesRowBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Result) -> Unit)? = null

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        val movie = differ.currentList[position]
        holder.bind(movie = movie)
    }

    fun setOnItemClickListener(listener: (Result) -> Unit) {
        onItemClickListener = listener
    }

}