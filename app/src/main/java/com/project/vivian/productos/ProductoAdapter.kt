package com.project.vivian.productos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.vivian.model.Producto
import com.project.vivian.reservas.MisReservacionesAdapter
import kotlinx.android.synthetic.main.item_producto.view.*

class ProductoAdapter (val list: List<Producto>, val itemClickListener: ItemClickListener): RecyclerView.Adapter<ProductoViewHolder>() {

    interface ItemClickListener {
        fun onItemClickMasUno(holder: ProductoViewHolder)
        fun onItemClickMenosUno(holder: ProductoViewHolder)
        fun onItemClickAddProducto(productoSelected : Producto, holder: ProductoViewHolder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ProductoViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val productos: Producto = list[position]
        holder.bind(productos)

        holder.itemView.imgMasUno.setOnClickListener {
            itemClickListener.onItemClickMasUno(holder)
        }

        holder.itemView.imgMenosUno.setOnClickListener {
            itemClickListener.onItemClickMenosUno(holder)
        }

        holder.itemView.addProducto.setOnClickListener {
            itemClickListener.onItemClickAddProducto(productos,holder)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}