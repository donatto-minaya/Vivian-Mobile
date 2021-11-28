package com.project.vivian.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.vivian.model.Producto
import com.project.vivian.productos.ProductoViewHolder

class ProductosTopAdapter(val list: List<Producto>, val itemClickListener: HomeFragment): RecyclerView.Adapter<ProductosTopViewHolder>() {

    interface ItemClickListener {
        fun onItemClickMasUno(holder: ProductoViewHolder)
        fun onItemClickMenosUno(holder: ProductoViewHolder)
        fun onItemClickAddProducto(productoSelected : Producto, holder: ProductoViewHolder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductosTopViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ProductosTopViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ProductosTopViewHolder, position: Int) {
        val productos: Producto = list[position]
        holder.bind(productos)

    }

    override fun getItemCount(): Int {
        return list.size
    }


}