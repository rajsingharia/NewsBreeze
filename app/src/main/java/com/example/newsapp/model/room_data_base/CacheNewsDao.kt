package com.example.newsapp.model.room_data_base
import androidx.room.*

@Dao
interface CacheNewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToCacheNews(articles: CacheArticle)

    @Query("SELECT * FROM cache_news_table")
    suspend fun getAllCacheNews(): List<CacheArticle>

    @Query("SELECT * FROM cache_news_table ORDER BY publishedAt ASC")
    suspend fun getAllCacheNewsSortByDateTime(): List<CacheArticle>

    @Delete
    suspend fun deleteCacheNews(articles: CacheArticle)

    @Query("DELETE FROM cache_news_table")
    suspend fun deleteCompleteCacheTable()

    @Query("SELECT * FROM cache_news_table WHERE title LIKE :query")
    suspend fun filterCacheNews(query:String): List<CacheArticle>

}