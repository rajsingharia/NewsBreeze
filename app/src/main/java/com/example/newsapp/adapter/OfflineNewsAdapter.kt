package com.example.newsapp.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.model.news_response_model.Article
import com.example.newsapp.util.Constants.changeDateTimeFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class OfflineNewsAdapter(val listener:OnItemClickListener):RecyclerView.Adapter<OfflineNewsAdapter.NewsViewHolder> (){


    private val differCallBack=object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url==newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem==newItem
        }

    }

    val differ=AsyncListDiffer(this,differCallBack)

    inner class NewsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),View.OnClickListener{
        var article_urlToImage:ImageView=itemView.findViewById(R.id.article_urlToImageOffline)
        var article_title:TextView=itemView.findViewById(R.id.article_titleOffline)
        var article_date_time:TextView = itemView.findViewById(R.id.article_saved_date_time)

        init {
           itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position=adapterPosition

            if(position!=RecyclerView.NO_POSITION) {
                listener.OnItemClick(differ.currentList[position])
            }

        }

    }

    interface OnItemClickListener{
        fun OnItemClick(article: Article)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.news_single_row_offline,parent,false))
    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {

        val article = differ.currentList[position]

        val article_urlToImage:ImageView = holder.article_urlToImage
        val article_title:TextView = holder.article_title
        val article_date_time:TextView = holder.article_date_time

        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(article_urlToImage)
            article_title.text=article.title
            val formattedDate = changeDateTimeFormat(article.publishedAt)
            article_date_time.text = formattedDate
        }
    }

}