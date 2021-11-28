package com.project.vivian.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.project.vivian.R
import com.project.vivian.model.Producto

class ProductosTopViewHolder(inflater: LayoutInflater, parent: ViewGroup):
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_producto_top, parent, false)){

    private var productoImageView : ImageView? = null
    private var textNombreProducto: TextView? = null

    init {
        productoImageView = itemView.findViewById(R.id.imgProductoTop)
        textNombreProducto = itemView.findViewById(R.id.textTitleProductoTop)
    }

    fun bind(producto: Producto) {
        textNombreProducto?.text = producto.nombre

        val options = RequestOptions()
            .placeholder(R.drawable.anonymous_image)
            .error(R.drawable.anonymous_image)

        productoImageView?.let {
            Glide.with(it)
                .setDefaultRequestOptions(options)
                .load(producto.imgUrl)
                .into(it)
        }

    }
}