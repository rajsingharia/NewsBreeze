package com.example.newsapp.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.model.news_response_model.Article
import com.example.newsapp.util.Constants.changeDateTimeFormat
import okio.utf8Size
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter:RecyclerView.Adapter<NewsAdapter.NewsViewHolder>(){

    private val differCallBack=object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url==newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem==newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallBack)

    inner class NewsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var article_row = itemView
        var article_urlToImage:ImageView=itemView.findViewById(R.id.article_urlToImage)
        var article_title:TextView=itemView.findViewById(R.id.article_title)
        var article_description:TextView=itemView.findViewById(R.id.article_description)
        var article_date_time:TextView=itemView.findViewById(R.id.article_date_time)
        val saveButton:Button = itemView.findViewById(R.id.article_save_button)
        val readButton:Button = itemView.findViewById(R.id.article_read_button)
    }

    private var onSaveClickListener : ((Article) -> Unit)? = null

    private var onItemClickListener : ((Article) -> Unit)? = null

    private var onReadClickListener : ((Article) -> Unit)? = null

    fun setOnSaveClickListener(listener: (Article) -> Unit) {
        onSaveClickListener = listener
    }

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    fun setReadItemClickListener(listener: (Article) -> Unit) {
        onReadClickListener = listener
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.news_single_row,parent,false))
    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {

        val article=differ.currentList[position]

        val article_urlToImage:ImageView=holder.article_urlToImage
        val article_title:TextView=holder.article_title
        val article_description:TextView=holder.article_description

        holder.apply {
            Glide.with(article_urlToImage.context).load(article.urlToImage).into(article_urlToImage)
            article_title.text=article.title
            article_description.text=article.description
            val date = changeDateTimeFormat(article.publishedAt)
            article_date_time.text=date.toString()

            saveButton.setOnClickListener {
                onSaveClickListener?.let{it(article)}
            }

            article_row.setOnClickListener {
                onItemClickListener?.let{it(article)}
            }
            readButton.setOnClickListener {
                onReadClickListener?.let{it(article)}
            }

        }
    }
}