package com.example.movieapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.movieapp.R
import com.example.movieapp.adapter.NowPlayingMoviesAdapter
import com.example.movieapp.adapter.UpComingMoviesAdapter
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.databinding.FragmentHomeBinding
import com.example.movieapp.util.Constants
import com.example.movieapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    val TAG = "MovieFragment"

    private lateinit var binding: FragmentHomeBinding
    lateinit var nowPlayingMoviesAdapter: NowPlayingMoviesAdapter
    lateinit var upComingMoviesAdapter: UpComingMoviesAdapter

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        initData()
        initObservers()
        initListener()
        return binding.root
    }

    private fun initListener() {
        nowPlayingMoviesAdapter.setOnItemClickListener { movie ->
            navigateDetailPage(movie.id)
        }

        upComingMoviesAdapter.setOnItemClickListener { movie ->
            navigateDetailPage(movie.id)
        }

        binding.swipeRefresh.setOnRefreshListener {
            binding.upcomingMovies.visibility = View.GONE
            binding.movieRV.visibility = View.GONE
            showProgressBar()
            showProgressBarForViewPager()
            viewModel.getUpComingMovies()
            viewModel.getMovies()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun navigateDetailPage(id: Int) {
        val bundle = Bundle().apply {
            putInt("movieId", id)
        }

        Navigation.findNavController(this.requireView())
            .navigate(
                R.id.action_homeFragment_to_detailFragment,
                bundle
            )
    }

    private fun initData() {
        viewModel.getUpComingMovies()
        viewModel.getMovies()
        setupNowPlayingRecyclerView()
        setupUpComingMoviesPlayingViewPager()
    }

    private fun initObservers() {
        viewModel.moviesResponse.observe(viewLifecycleOwner, Observer { response ->
            binding.movieRV.visibility = View.VISIBLE
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { moviesResponse ->
                        nowPlayingMoviesAdapter.differ.submitList(moviesResponse.results.toList())
                        val totalPages =
                            moviesResponse.total_pages / Constants.QUERY_PAGE_SIZE + 2
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

        viewModel.upComingResponse.observe(viewLifecycleOwner, Observer { response ->
            binding.upcomingMovies.visibility = View.VISIBLE
            when (response) {
                is Resource.Success -> {
                    hideProgressBarForViewPager()
                    response.data?.let { moviesResponse ->
                        upComingMoviesAdapter.differ.submitList(
                            moviesResponse.results.take(5).toList()
                        )
                    }
                }

                is Resource.Error -> {
                    hideProgressBarForViewPager()
                    response.message?.let { message ->
                        Toast.makeText(
                            requireContext(),
                            "An Error Occured: $message",
                            Toast.LENGTH_SHORT
                        )
                    }
                }

                is Resource.Loading -> {
                    showProgressBarForViewPager()
                }

                else -> {}
            }
        })
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideProgressBarForViewPager() {
        binding.paginationProgressBar2.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBarForViewPager() {
        binding.paginationProgressBar2.visibility = View.VISIBLE
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

    private fun setupNowPlayingRecyclerView() {
        nowPlayingMoviesAdapter = NowPlayingMoviesAdapter()
        binding.movieRV.apply {
            adapter = nowPlayingMoviesAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@HomeFragment.scrollListener)
        }
    }

    private fun setupUpComingMoviesPlayingViewPager() {
        upComingMoviesAdapter = UpComingMoviesAdapter()
        binding.upcomingMovies.apply {
            adapter = upComingMoviesAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
        binding.dotsIndicator.attachTo(binding.upcomingMovies)
    }

}