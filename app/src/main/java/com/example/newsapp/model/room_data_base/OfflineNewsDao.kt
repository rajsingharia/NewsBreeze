package com.example.newsapp.model.room_data_base
import androidx.room.*

@Dao
interface OfflineNewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToOfflineNews(articles: OfflineArticle)

    @Query("SELECT * FROM offline_news_table")
    suspend fun getAllOfflineNews(): List<OfflineArticle>

    @Query("SELECT * FROM offline_news_table ORDER BY publishedAt ASC")
    suspend fun getAllOfflineNewsSortByDateTime(): List<OfflineArticle>

    @Delete
    suspend fun deleteOfflineNews(articles: OfflineArticle)

    @Query("SELECT * FROM offline_news_table WHERE title LIKE :query")
    suspend fun filterOfflineNews(query:String): List<OfflineArticle>

}