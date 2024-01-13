package com.example.movieapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.databinding.NowPlayingMoviesRowBinding
import com.example.movieapp.models.nowplayingmovies.Movie
import com.example.movieapp.util.Constants

class PopularMoviesAdapter : RecyclerView.Adapter<PopularMoviesAdapter.MoviesViewHolder>() {

    inner class MoviesViewHolder(private val binding: NowPlayingMoviesRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            Glide.with(binding.root).load(Constants.BASE_IMAGE_URL + movie.posterPath)
                .into(binding.movieId)
            binding.movieName.text =
                movie.originalTitle + "(" + parseYear(movie.releaseDate) + ")"
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

    private val differCallback = object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
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

    private var onItemClickListener: ((Movie) -> Unit)? = null

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        val movie = differ.currentList[position]
        holder.bind(movie = movie)
    }

    fun setOnItemClickListener(listener: (Movie) -> Unit) {
        onItemClickListener = listener
    }

}