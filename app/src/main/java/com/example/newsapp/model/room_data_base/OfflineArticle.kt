package com.example.newsapp.model.room_data_base
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_news_table")
data class OfflineArticle (
        @ColumnInfo var author: String?,
        @ColumnInfo var content: String?,
        @ColumnInfo var description: String?,
        @ColumnInfo var publishedAt: String?,
        @PrimaryKey @ColumnInfo var title: String,
        @ColumnInfo var url: String?,
        @ColumnInfo var urlToImage: String?
        //@ColumnInfo var name:String?
)