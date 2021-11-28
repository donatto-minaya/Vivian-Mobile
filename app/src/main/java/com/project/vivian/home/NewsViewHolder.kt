package com.project.vivian.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.project.vivian.R

class NewsViewHolder(inflater: LayoutInflater, parent: ViewGroup):
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_news, parent, false)){

    private var imageView: ImageView? = null
    private var textTitle: TextView? = null

    init {
        imageView = itemView.findViewById(R.id.imgNew)
        textTitle = itemView.findViewById(R.id.textTitleNew)
    }

    fun bind(news: News) {
        textTitle?.text = news.title

        val options = RequestOptions()
            .placeholder(R.drawable.anonymous_image)
            .error(R.drawable.anonymous_image)

        imageView?.let {
            Glide.with(it)
                .setDefaultRequestOptions(options)
                .load(news.image)
                .into(it)

        }

    }
}