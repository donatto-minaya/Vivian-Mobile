package com.project.vivian.carrito

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.vivian.model.ItemCarrito

class CarritoAdapter  (val list: List<ItemCarrito>): RecyclerView.Adapter<CarritoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CarritoViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val itemCarrito: ItemCarrito = list[position]
        holder.bind(itemCarrito)

    }

    override fun getItemCount(): Int {
        return list.size
    }


}