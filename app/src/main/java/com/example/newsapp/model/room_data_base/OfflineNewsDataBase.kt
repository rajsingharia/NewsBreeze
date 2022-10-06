package com.example.newsapp.model.room_data_base

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CacheArticle::class, OfflineArticle::class],version = 1)
abstract class OfflineNewsDataBase:RoomDatabase() {

    abstract fun CacheNewsDao():CacheNewsDao
    abstract fun OfflineNewsDao():OfflineNewsDao

}