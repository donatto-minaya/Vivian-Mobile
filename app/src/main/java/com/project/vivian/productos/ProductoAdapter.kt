package com.project.vivian.productos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.vivian.model.Producto
import com.project.vivian.reservas.MisReservacionesAdapter

class ProductoAdapter (val list: List<Producto>, val itemClickListener: MisReservacionesAdapter.ItemClickListener): RecyclerView.Adapter<ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ProductoViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val productos: Producto = list[position]
        holder.bind(productos)
    }

    override fun getItemCount(): Int {
        return list.size
    }


}