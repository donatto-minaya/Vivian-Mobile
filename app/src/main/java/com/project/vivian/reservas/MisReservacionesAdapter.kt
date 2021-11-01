package com.project.vivian.reservas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.vivian.model.Reserva
import kotlinx.android.synthetic.main.item_reserva.view.*

class MisReservacionesAdapter(val list: List<Reserva>, val itemClickListener: ItemClickListener): RecyclerView.Adapter<MisReservacionesViewHolder>() {

    interface ItemClickListener {
        fun onItemClickNoteUpdate(reservaSelected: Reserva)
        fun onItemClickNoteDelete(reservaSelected: Reserva)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MisReservacionesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MisReservacionesViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MisReservacionesViewHolder, position: Int) {
        val itemReserva: Reserva = list[position]
        holder.bind(itemReserva)

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClickNoteUpdate(itemReserva)
        }

        holder.itemView.imgDelete.setOnClickListener {
            itemClickListener.onItemClickNoteDelete(itemReserva)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}