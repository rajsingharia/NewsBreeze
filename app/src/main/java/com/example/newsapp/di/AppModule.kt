package com.example.newsapp.di

import android.content.Context
import androidx.room.Room
import com.example.newsapp.BuildConfig
import com.example.newsapp.api.NewsApiService
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.model.room_data_base.CacheNewsDao
import com.example.newsapp.model.room_data_base.OfflineNewsDao
import com.example.newsapp.model.room_data_base.OfflineNewsDataBase
import com.example.newsapp.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providesBaseUrl() = BASE_URL

    @Singleton
    @Provides
    fun provideOkHttpClient() = if (BuildConfig.DEBUG){
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }else{
        OkHttpClient
            .Builder()
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, BASE_URL:String) : Retrofit = Retrofit
        .Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): NewsApiService = retrofit.create(NewsApiService::class.java)

    @Singleton
    @Provides
    fun providesRepository(apiService: NewsApiService,
                           cacheNewsDao: CacheNewsDao,
                           offlineNewsDao: OfflineNewsDao) = NewsRepository(apiService,cacheNewsDao,offlineNewsDao)

    @Singleton
    @Provides
    fun provideCacheNewsDao(offlineNewsDataBase: OfflineNewsDataBase) : CacheNewsDao {
        return offlineNewsDataBase.CacheNewsDao()
    }

    @Singleton
    @Provides
    fun provideOfflineNewsDao(offlineNewsDataBase: OfflineNewsDataBase) : OfflineNewsDao {
        return offlineNewsDataBase.OfflineNewsDao()
    }

    @Singleton
    @Provides
    fun providesAppDatabase(@ApplicationContext appContext: Context) : OfflineNewsDataBase {
        return Room.databaseBuilder(
            appContext,
            OfflineNewsDataBase::class.java,
            "offline_database"
        ).build()
    }

}