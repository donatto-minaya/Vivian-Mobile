package com.project.vivian.carrito

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.project.vivian.R
import com.project.vivian.model.ItemCarrito

class CarritoViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(
    R.layout.item_carrito_compras, parent, false)) {

    private var imgProductoCarrito : ImageView? = null
    private var textNombreProductoCarrito: TextView? = null
    private var textPrecioNumeroCarrito: TextView? = null
    private var textCantidadNumeroCarrito: TextView? = null


    init {
        imgProductoCarrito = itemView.findViewById(R.id.imgProductoCarrito)
        textNombreProductoCarrito = itemView.findViewById(R.id.textNombreProductoCarrito)
        textPrecioNumeroCarrito = itemView.findViewById(R.id.textPrecioNumeroCarrito)
        textCantidadNumeroCarrito = itemView.findViewById(R.id.textCantidadNumeroCarrito)
    }

    fun bind(itemCarrito: ItemCarrito){
        textNombreProductoCarrito?.text = itemCarrito.producto?.nombre
        textPrecioNumeroCarrito?.text = itemCarrito.preciototal.toString()
        textCantidadNumeroCarrito?.text = itemCarrito.cantidad.toString()

        val options = RequestOptions()
            .placeholder(R.drawable.anonymous_image)
            .error(R.drawable.anonymous_image)

        imgProductoCarrito?.let {
            Glide.with(it)
                .setDefaultRequestOptions(options)
                .load(itemCarrito.producto?.imgUrl)
                .into(it)

        }
    }
}