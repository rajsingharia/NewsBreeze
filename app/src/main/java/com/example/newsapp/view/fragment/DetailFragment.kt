package com.example.newsapp.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.newsapp.MainActivity
import com.example.newsapp.databinding.FragmentDetailBinding
import com.example.newsapp.model.news_response_model.Article
import com.example.newsapp.model.room_data_base.OfflineArticle
import com.example.newsapp.util.Constants.changeDateTimeFormat
import com.example.newsapp.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class DetailFragment : Fragment() {

    private val args: DetailFragmentArgs by navArgs()
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater,container,false)
        newsViewModel = (activity as MainActivity).newsViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setData()
        binding.articleDetailBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.articleSaveButton.setOnClickListener {
            val article = Article(args.articleAuthor,args.articleContent,args.articleDescription,args.articleDateTime,args.articleTitle,args.articleUrl,args.articleImageUrl)
            saveArticle(article)
        }

    }

    private fun setData() {
        Glide.with(requireActivity()).load(args.articleImageUrl).into(binding.articleDetailImage)
        binding.articleDetailTitle.text = args.articleTitle
        binding.articleDetailDescription.text = args.articleDescription
        val date = changeDateTimeFormat(args.articleDateTime)
        binding.articleDetailTime.text = date.toString()
    }

    private fun saveArticle(articles: Article) {
        val offlineArticle = OfflineArticle(articles.author,articles.content,articles.description,articles.publishedAt,articles.title,articles.url,articles.urlToImage)
        newsViewModel.insertOfflineNewsArticle(offlineArticle)
        Snackbar.make(binding.root,"Article Saved", Snackbar.LENGTH_SHORT).apply { show() }
    }

}