package com.example.newsapp.model.news_response_model

data class NewsResponse(
    val articles: List<Article>,
    val totalResults: Int
)