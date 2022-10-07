package com.example.newsapp.repository
import com.example.newsapp.api.NewsApiService
import com.example.newsapp.model.news_response_model.Article
import com.example.newsapp.model.news_response_model.NewsResponse
import com.example.newsapp.model.room_data_base.CacheNewsDao
import com.example.newsapp.model.room_data_base.CacheArticle
import com.example.newsapp.model.room_data_base.OfflineArticle
import com.example.newsapp.model.room_data_base.OfflineNewsDao
import com.example.newsapp.util.NetworkResult
import java.io.IOException
import javax.inject.Inject


class NewsRepository @Inject constructor(
   private val apiService: NewsApiService,
   private val cacheNewsDao: CacheNewsDao,
   private val offlineNewsDao: OfflineNewsDao
) {
   suspend fun getBreakingNews(countryCode:String,pageNumber:Int): NetworkResult<NewsResponse> {
      return try {
         val response = apiService.getBreakingNews(countryCode,pageNumber)
         if(response.isSuccessful) {
            insertOfflineForCaching(response.body()!!,cacheNewsDao)
            val articles = getCacheData(cacheNewsDao)
            NetworkResult.Success(articles)
         }
         else {
            val articles = getCacheData(cacheNewsDao)
            NetworkResult.Error(response.message(),articles)
         }
      } catch (e: IOException) {
         val articles = getCacheData(cacheNewsDao)
         NetworkResult.Error("Check Internet Connection",articles)
      }
   }

   private suspend fun getCacheData(cacheNewsDao:CacheNewsDao): NewsResponse {
      val response = cacheNewsDao.getAllCacheNews()
      //return response
      val news:MutableList<Article> = mutableListOf()
      for (r in response) {
         val article = Article(r.author.toString(),
            r.content.toString(),
            r.description.toString(),
            r.publishedAt.toString(),
            r.title,
            r.url.toString(),
            r.urlToImage.toString())
         news.add(article)
      }
      return NewsResponse(news,news.size)
   }

   private suspend fun insertOfflineForCaching(response: NewsResponse, cacheNewsDao:CacheNewsDao) {
      cacheNewsDao.deleteCompleteCacheTable()
      for(article in response.articles) {
         val offlineNewsEntity = CacheArticle(
            article.author,
            article.content,
            article.description,
            article.publishedAt,
            article.title,
            article.url,
            article.urlToImage)
         cacheNewsDao.insertToCacheNews(offlineNewsEntity)
      }
   }

   suspend fun getCacheBreakingNews(): NetworkResult<NewsResponse> {
      val response = cacheNewsDao.getAllCacheNews()

      val news:MutableList<Article> = mutableListOf()
      for (r in response) {
         val article = Article(r.author.toString(),r.content.toString(),r.description.toString(),r.publishedAt.toString(),r.title,r.url.toString(),r.urlToImage.toString())
         news.add(article)
      }
      return NetworkResult.Success(NewsResponse(news,news.size))
   }

   suspend fun searchCacheNewsArticle(query:String): NetworkResult<NewsResponse> {

      val response = cacheNewsDao.filterCacheNews(query)

      val news:MutableList<Article> = mutableListOf()
      for (r in response) {
         val article = Article(r.author.toString(),r.content.toString(),r.description.toString(),r.publishedAt.toString(),r.title,r.url.toString(),r.urlToImage.toString())
         news.add(article)
      }
      return NetworkResult.Success(NewsResponse(news,news.size))
   }

   suspend fun sortCacheNewsByDateTime(): NetworkResult<NewsResponse> {

      val response = cacheNewsDao.getAllCacheNewsSortByDateTime()

      val news:MutableList<Article> = mutableListOf()
      for (r in response) {
         val article = Article(r.author.toString(),r.content.toString(),r.description.toString(),r.publishedAt.toString(),r.title,r.url.toString(),r.urlToImage.toString())
         news.add(article)
      }
      return NetworkResult.Success(NewsResponse(news,news.size))
   }

   suspend fun insertOfflineNewsArticle(article: OfflineArticle) {
      offlineNewsDao.insertToOfflineNews(article)
   }

   suspend fun getOfflineData(): NetworkResult<NewsResponse> {
      val response = offlineNewsDao.getAllOfflineNews()
      val news:MutableList<Article> = mutableListOf()
      for (r in response) {
         val article = Article(r.author.toString(),r.content.toString(),r.description.toString(),r.publishedAt.toString(),r.title,r.url.toString(),r.urlToImage.toString())
         news.add(article)
      }
      return NetworkResult.Success(NewsResponse(news,news.size))
   }

   suspend fun sortOfflineNewsByDateTime(): NetworkResult<NewsResponse> {

      val response = offlineNewsDao.getAllOfflineNewsSortByDateTime()

      val news:MutableList<Article> = mutableListOf()
      for (r in response) {
         val article = Article(r.author.toString(),r.content.toString(),r.description.toString(),r.publishedAt.toString(),r.title,r.url.toString(),r.urlToImage.toString())
         news.add(article)
      }
      return NetworkResult.Success(NewsResponse(news,news.size))
   }

   suspend fun deleteOfflineArticle(article: OfflineArticle) {
      offlineNewsDao.deleteOfflineNews(article)
   }

   suspend fun searchOfflineNewsArticle(query:String): NetworkResult<NewsResponse> {

      val response = offlineNewsDao.filterOfflineNews(query)

      val news:MutableList<Article> = mutableListOf()
      for (r in response) {
         val article = Article(r.author.toString(),r.content.toString(),r.description.toString(),r.publishedAt.toString(),r.title,r.url.toString(),r.urlToImage.toString())
         news.add(article)
      }
      return NetworkResult.Success(NewsResponse(news,news.size))
   }

}