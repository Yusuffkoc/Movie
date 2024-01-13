package com.example.movieapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.databinding.UpcomingMoviesRowBinding
import com.example.movieapp.util.Constants

class UpComingMoviesAdapter : RecyclerView.Adapter<UpComingMoviesAdapter.MoviesViewHolder>() {

    inner class MoviesViewHolder(private val binding: UpcomingMoviesRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: com.example.movieapp.models.upcomingmovies.Result) {
            Glide.with(binding.root).load(Constants.BASE_IMAGE_URL + movie.poster_path)
                .into(binding.movieImage)
            binding.movieName.text =
                movie.original_title + "(" + parseYear(movie.release_date) + ")"
            binding.movieDetail.text = movie.overview

            binding.root.setOnClickListener {
                onItemClickListener?.let { it(movie) }
            }
        }
    }

    private fun parseYear(releaseDate: String?): CharSequence? {
        val date = releaseDate?.split("-")
        return date?.get(0)
    }

    private val differCallback = object : DiffUtil.ItemCallback<com.example.movieapp.models.upcomingmovies.Result>() {
        override fun areItemsTheSame(oldItem: com.example.movieapp.models.upcomingmovies.Result, newItem: com.example.movieapp.models.upcomingmovies.Result): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: com.example.movieapp.models.upcomingmovies.Result, newItem: com.example.movieapp.models.upcomingmovies.Result): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        return MoviesViewHolder(
            UpcomingMoviesRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((com.example.movieapp.models.upcomingmovies.Result) -> Unit)? = null

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        val movie = differ.currentList[position]
        holder.bind(movie = movie)
    }

    fun setOnItemClickListener(listener: (com.example.movieapp.models.upcomingmovies.Result) -> Unit) {
        onItemClickListener = listener
    }

}