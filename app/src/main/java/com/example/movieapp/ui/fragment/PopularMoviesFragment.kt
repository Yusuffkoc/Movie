package com.example.movieapp.ui.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.adapter.NowPlayingMoviesAdapter
import com.example.movieapp.adapter.PopularMoviesAdapter
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.databinding.FragmentDetailBinding
import com.example.movieapp.databinding.FragmentHomeBinding
import com.example.movieapp.databinding.FragmentPopularMoviesBinding
import com.example.movieapp.util.Constants
import com.example.movieapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PopularMoviesFragment : BaseFragment() {

    companion object {
        fun newInstance() = PopularMoviesFragment()
    }

    val TAG = "MovieFragment"

    private lateinit var binding: FragmentPopularMoviesBinding
    lateinit var popularMoviesAdapter: PopularMoviesAdapter

    private val viewModel: PopularMoviesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPopularMoviesBinding.inflate(inflater, container, false)
        initUI()
        initData()
        initListener()
        initObserver()
        return binding.root
    }

    private fun initObserver() {
        viewModel.moviesResponse.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { moviesResponse ->
                        popularMoviesAdapter.differ.submitList(moviesResponse.movies.toList())
                        val totalPages =
                            moviesResponse.totalPages / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.moviesPage == totalPages
                        if (isLastPage) {
                            binding.movieRV.setPadding(0, 0, 0, 0)
                        }
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        showToast("${getString(R.string.base_error_message)}$message")
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }

                else -> {}
            }
        })
    }

    private fun initData() {
        viewModel.getMovies()
        setupRecyclerView()
        setStatusBarHeightMargin()
    }

    private fun setStatusBarHeightMargin() {
        val statusBarHeightId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight = resources.getDimensionPixelSize(statusBarHeightId)
        val params = binding.pageRl.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = statusBarHeight
        binding.pageRl.layoutParams = params
    }

    private fun initListener() {
        binding.backLayoutId.backButtonId.setOnClickListener {
            Navigation.findNavController(this.requireView())
                .navigate(R.id.action_popularMoviesFragment_to_homeFragment)
        }

        popularMoviesAdapter.setOnItemClickListener { movie ->
            val bundle = Bundle().apply {
                putInt("movieId", movie.id)
            }

            Navigation.findNavController(this.requireView())
                .navigate(
                    R.id.action_popularMoviesFragment_to_detailFragment,
                    bundle
                )
        }
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.root.findNavController().popBackStack(R.id.homeFragment, false)
            }
        })
    }

    private fun initUI() {
        binding.backLayoutId.pageNameId.text = getString(R.string.popular_page)
    }


    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }


    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.itemCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.getMovies()
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView() {
        popularMoviesAdapter = PopularMoviesAdapter()
        binding.movieRV.apply {
            adapter = popularMoviesAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@PopularMoviesFragment.scrollListener)
        }
    }


}