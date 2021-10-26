package com.project.vivian.reservas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.vivian.model.Reserva

class MisReservacionesAdapter(val list: List<Reserva>): RecyclerView.Adapter<MisReservacionesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MisReservacionesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MisReservacionesViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MisReservacionesViewHolder, position: Int) {
        val itemReserva: Reserva = list[position]
        holder.bind(itemReserva)
    }

    override fun getItemCount(): Int {
        return list.size
    }


}