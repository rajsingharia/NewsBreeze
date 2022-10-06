package com.example.newsapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.MainActivity
import com.example.newsapp.R
import com.example.newsapp.adapter.OfflineNewsAdapter
import com.example.newsapp.databinding.FragmentSaveBinding
import com.example.newsapp.model.news_response_model.Article
import com.example.newsapp.model.room_data_base.OfflineArticle
import com.example.newsapp.util.NetworkResult
import com.example.newsapp.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.*

class SavedFragment : Fragment() ,OfflineNewsAdapter.OnItemClickListener{

    private lateinit var newsViewModel:NewsViewModel
    private lateinit var binding: FragmentSaveBinding
    private lateinit var adapter: OfflineNewsAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaveBinding.inflate(inflater,container,false)
        newsViewModel = (activity as MainActivity).newsViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = OfflineNewsAdapter(this)

        newsViewModel.getOfflineNewsArticle()
        binding.rvOfflineSavedNews.adapter=adapter
        val linearLayoutManager = LinearLayoutManager(context)
        binding.rvOfflineSavedNews.layoutManager = linearLayoutManager

        newsViewModel.savedNews.observe(requireActivity()) {
            it?.let {
               when(it) {
                   is NetworkResult.Loading -> {
                       binding.offlineNewsProcessBar.visibility = View.VISIBLE
                       binding.rvOfflineSavedNews.visibility = View.GONE
                   }
                   is NetworkResult.Success -> {
                       binding.offlineNewsProcessBar.visibility = View.GONE
                       binding.rvOfflineSavedNews.visibility = View.VISIBLE
                       adapter.differ.submitList(it.data?.articles)
                   }
                   is NetworkResult.Error -> {
                       binding.offlineNewsProcessBar.visibility = View.GONE
                       binding.rvOfflineSavedNews.visibility = View.VISIBLE
                       adapter.differ.submitList(it.data?.articles)
                   }
               }
            }
        }

        binding.articleSavedBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.breakingNewsDateTimeSort.isSelected = newsViewModel.isSortByDateOffline

        binding.sortIcon.setOnClickListener {
            if(binding.sortPopUpMenu.visibility==View.GONE)
                showPopUpMenu()
            else
                hidePopUpMenu()
        }
        binding.breakingNewsDateTimeSort.setOnCheckedChangeListener { _,isChecked ->
            if(isChecked) {
                newsViewModel.sortOfflineNewsArticle()
            }
            else {
                newsViewModel.getOfflineNewsArticle()
            }
            newsViewModel.isSortByDateOffline = !newsViewModel.isSortByDateOffline
            binding.breakingNewsDateTimeSort.isSelected = newsViewModel.isSortByDateOffline
            scrollRecyclerViewToTop()
        }

        binding.savedNewsSearchEditText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.savedNewsSearchEditText.clearFocus()
                val search: String? =query
                if (search != null) {
                    searchQuery(query)
                }
                return false
            }
            override fun onQueryTextChange(query: String?): Boolean {
                if(!query.isNullOrEmpty()) {
                    linearLayoutManager.scrollToPositionWithOffset(0, 0)
                    searchQuery(query)
                }
                return false
            }
        })


        val itemTouchHelperCallBack=object :ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
              return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val articles = adapter.differ.currentList[viewHolder.adapterPosition]
                val offlineArticle = OfflineArticle(articles.author,articles.content,articles.description,articles.publishedAt,articles.title,articles.url,articles.urlToImage)
                newsViewModel.deleteOfflineNewsArticle(offlineArticle)
                Snackbar.make(view,"Deleted From Saved Articles", Snackbar.LENGTH_SHORT).apply { show() }
            }
        }
        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding.rvOfflineSavedNews)
        }
    }

    private fun searchQuery(query: String) {
        newsViewModel.filterOfflineNewsArticle(query)
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

    override fun OnItemClick(articles: Article) {
        val action = SavedFragmentDirections.actionSavedFragmentToDetailFragment(articles.publishedAt,articles.title,articles.description,articles.urlToImage,articles.author,articles.content,articles.url)
        findNavController().navigate(action)
    }

    private fun scrollRecyclerViewToTop() {
        binding.rvOfflineSavedNews.post {
            binding.rvOfflineSavedNews.smoothScrollToPosition(0)
        }
    }
}