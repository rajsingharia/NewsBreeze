package com.example.newsapp.viewmodel

import androidx.lifecycle.*
import com.example.newsapp.model.news_response_model.Article
import com.example.newsapp.model.news_response_model.NewsResponse
import com.example.newsapp.model.repository.NewsRepository
import com.example.newsapp.model.room_data_base.OfflineArticle
import com.example.newsapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
    ): ViewModel(){

    val breakingNews = MutableLiveData<NetworkResult<NewsResponse>>()
    val savedNews = MutableLiveData<NetworkResult<NewsResponse>>()
    private var breakingNewsPageNo:Int = 1
    private val countryCode:String="in"
    var isSortByDate:Boolean = false
    var isSortByDateOffline:Boolean = false

    //ONLINE
    fun getBreakingNews() = viewModelScope.launch {
        //breakingNewsPageNo++
        breakingNews.value = NetworkResult.Loading()
        newsRepository.getBreakingNews(countryCode,breakingNewsPageNo).let{ response->
            breakingNews.value = response
        }
    }

    fun getCacheBreakingNews() = viewModelScope.launch {
        breakingNews.value = NetworkResult.Loading()
        newsRepository.getCacheBreakingNews().let { response->
            breakingNews.value = response
        }
    }

    fun searchCacheNews(search:String)=viewModelScope.launch {
        breakingNews.value = NetworkResult.Loading()
        val response =  newsRepository.searchCacheNewsArticle("%${search}%")
        breakingNews.postValue(response)
    }

    fun sortCacheNewsByDateTime() = viewModelScope.launch {
        breakingNews.value = NetworkResult.Loading()
        newsRepository.sortCacheNewsByDateTime().let { response->
            breakingNews.value = response
        }
    }

    fun insertOfflineNewsArticle(article: OfflineArticle) = viewModelScope.launch {
        newsRepository.insertOfflineNewsArticle(article)
    }

    fun getOfflineNewsArticle() = viewModelScope.launch {
        savedNews.value = NetworkResult.Loading()
        newsRepository.getOfflineData().let{ response->
            savedNews.value = response
        }
    }

    fun deleteOfflineNewsArticle(article: OfflineArticle) = viewModelScope.launch {
        newsRepository.deleteOfflineArticle(article)
    }

    fun sortOfflineNewsArticle() = viewModelScope.launch {
        savedNews.value = NetworkResult.Loading()
        newsRepository.sortOfflineNewsByDateTime().let { response->
            savedNews.value = response
        }
    }

    fun filterOfflineNewsArticle(search:String) = viewModelScope.launch {
        savedNews.value = NetworkResult.Loading()
        val response =  newsRepository.searchOfflineNewsArticle("%${search}%")
        savedNews.postValue(response)
    }

}
