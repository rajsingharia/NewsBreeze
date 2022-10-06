package com.example.newsapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.MainActivity
import com.example.newsapp.R
import com.example.newsapp.adapter.NewsAdapter
import com.example.newsapp.databinding.FragmentHomeBinding
import com.example.newsapp.model.news_response_model.Article
import com.example.newsapp.model.room_data_base.OfflineArticle
import com.example.newsapp.util.NetworkResult
import com.example.newsapp.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar


class HomeFragment : Fragment() {

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var binding:FragmentHomeBinding
    private lateinit var adapter: NewsAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding= FragmentHomeBinding.inflate(inflater,container,false)
        newsViewModel = (activity as MainActivity).newsViewModel
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter=NewsAdapter()
        binding.rvBreakingNews.adapter = adapter
        binding.rvBreakingNews.layoutManager = LinearLayoutManager(context)

        listenToSearchBox()

        observeBreakingNews()

        binding.breakingNewsDateTimeSort.isSelected = newsViewModel.isSortByDate

        binding.sortIcon.setOnClickListener {
            if(binding.sortPopUpMenu.visibility==View.GONE)
                showPopUpMenu()
            else
                hidePopUpMenu()
        }
        binding.breakingNewsDateTimeSort.setOnCheckedChangeListener { _,isChecked ->
            if(isChecked) {
                newsViewModel.sortCacheNewsByDateTime()
            }
            else {
                newsViewModel.getCacheBreakingNews()
            }
            newsViewModel.isSortByDate = !newsViewModel.isSortByDate
        }

        binding.breakingNewsSwipeToRefresh.setOnRefreshListener {
            binding.breakingNewsDateTimeSort.isChecked = false
            newsViewModel.getBreakingNews()
        }

        adapter.setOnSaveClickListener {
            onSaveClick(it)
        }

        adapter.setOnItemClickListener {
            onItemClick(it)
        }

        adapter.setReadItemClickListener {
            onReadClick(it)
        }

    }

    private fun observeBreakingNews() {
        newsViewModel.breakingNews.observe(requireActivity()) {
            it?.let {
                binding.breakingNewsSwipeToRefresh.isRefreshing = false
                when (it) {
                    is NetworkResult.Loading -> {
                        binding.breakingNewsProcessBar.visibility = View.VISIBLE
                        binding.rvBreakingNews.visibility = View.GONE
                    }
                    is NetworkResult.Success -> {
                        binding.breakingNewsProcessBar.visibility = View.GONE
                        binding.rvBreakingNews.visibility = View.VISIBLE
                        adapter.differ.submitList(it.data!!.articles)
                        scrollRecyclerViewToTop()
                    }
                    else -> {
                        binding.breakingNewsProcessBar.visibility = View.GONE
                        binding.rvBreakingNews.visibility = View.VISIBLE
                        adapter.differ.submitList(it.data!!.articles)
                    }
                }
            }
        }
    }

    private fun listenToSearchBox() {
        binding.breakingNewsSearchEditText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.breakingNewsSearchEditText.clearFocus()
                val search: String? =query
                if (search != null) {
                    searchQuery(query)
                }
                return false
            }
            override fun onQueryTextChange(query: String?): Boolean {
                if(!query.isNullOrEmpty()) {
                    searchQuery(query)
                }
                return false
            }
        })
    }

    private fun hidePopUpMenu() {
        binding.sortPopUpMenu.visibility = View.GONE
    }

    private fun showPopUpMenu() {
        binding.sortPopUpMenu.startAnimation(
            AnimationUtils.loadAnimation(
                binding.root.context,
                R.anim.slide_down
            )
        )
        binding.sortPopUpMenu.visibility = View.VISIBLE
    }

    private fun searchQuery(query: String) {
        newsViewModel.searchCacheNews(query)
    }

    private fun onReadClick(articles: Article) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(articles.publishedAt,articles.title,articles.description,articles.urlToImage,articles.author,articles.content,articles.url)
        findNavController().navigate(action)
    }

    private fun onSaveClick(articles: Article) {
        val offlineArticle = OfflineArticle(articles.author,articles.content,articles.description,articles.publishedAt,articles.title,articles.url,articles.urlToImage)
        newsViewModel.insertOfflineNewsArticle(offlineArticle)
        Snackbar.make(binding.root,"Article Saved", Snackbar.LENGTH_SHORT).apply { show() }
    }

    private fun onItemClick(articles: Article) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(articles.publishedAt,articles.title,articles.description,articles.urlToImage,articles.author,articles.content,articles.url)
        findNavController().navigate(action)
    }

    private fun scrollRecyclerViewToTop() {
        binding.rvBreakingNews.post {
            binding.rvBreakingNews.smoothScrollToPosition(0)
        }
    }

}