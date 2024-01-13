package com.example.movieapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.databinding.FragmentDetailBinding
import com.example.movieapp.util.Constants
import com.example.movieapp.util.Constants.BASE_IMAGE_URL
import com.example.movieapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : BaseFragment() {

    companion object {
        fun newInstance() = DetailFragment()
    }

    private lateinit var binding: FragmentDetailBinding
    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        initData()
        initListener()
        initObservers()
        return binding.root
    }

    private fun initObservers() {
        viewModel.moviesResponse.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideLoading()
                    response.data?.let { movie ->
                        binding.movieTitle.text = movie.title
                        binding.movieDescription.text = movie.overview
                        binding.movieDate.text = movie.release_date
                        binding.rating.text = parseRating(movie.vote_average)
                        Glide.with(this).load(BASE_IMAGE_URL + movie.poster_path)
                            .into(binding.movieImage)
                    }
                }

                is Resource.Error -> {
                    hideLoading()
                    response.message?.let { message ->
                        showToast("${getString(R.string.base_error_message)}$message")
                    }
                }

                is Resource.Loading -> {
                    showLoading()
                }

                else -> {}
            }
        })
    }

    private fun initListener() {
        binding.backButton.setOnClickListener {
            binding.root.findNavController().popBackStack(R.id.homeFragment, false)
        }

        binding.popularMoviesButton.setOnClickListener {
            Navigation.findNavController(this.requireView())
                .navigate(R.id.action_detailFragment_to_popularMoviesFragment)
        }
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    binding.root.findNavController().popBackStack(R.id.homeFragment, false)
                }
            })
    }

    private fun initData() {
        val movieId = arguments?.getInt("movieId")
        movieId?.let { viewModel.getMovies(movieId) }

    }

    private fun parseRating(it: Double): String {
        val number = it.toString().split(".")
        if (!number.isEmpty()) {
            if (number.get(1).isEmpty()) {
                return number.get(0)
            } else
                return number.get(0) + "." + number.get(0).first()
        } else {
            return it.toString()
        }

    }

}