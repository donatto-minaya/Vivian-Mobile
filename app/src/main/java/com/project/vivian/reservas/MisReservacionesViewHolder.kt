package com.project.vivian.reservas

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.vivian.R
import com.project.vivian.model.Reserva

class MisReservacionesViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_reserva, parent, false)) {

    private var textDni: TextView? = null
    private var textNombreCliente: TextView? = null
    private var textFecha: TextView? = null
    private var textTurno: TextView? = null
    private var textMesa: TextView? = null
    private var qrImageView : ImageView? = null

    init {
        textDni = itemView.findViewById(R.id.txtDni)
        textNombreCliente = itemView.findViewById(R.id.txtNombreCliente)
        textFecha = itemView.findViewById(R.id.textDate)
        textTurno = itemView.findViewById(R.id.txtTurno)
        textMesa = itemView.findViewById(R.id.txtMesa)
        qrImageView = itemView.findViewById(R.id.imgQrCode)
    }

    fun bind(reserva: Reserva){
        textDni?.text = reserva.dni
        textNombreCliente?.text = reserva.nombreCliente
        textFecha?.text = reserva.fecha
        textTurno?.text = reserva.turno
        textMesa?.text = reserva.mesa.capacidadPersonas.toString()

    }
}