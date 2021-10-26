package com.project.vivian.model

import com.google.firebase.database.Exclude

data class Reserva(val nombreCliente : String, val dni: String, val fecha : String, val mesa: Int, val turno: String, @Exclude val key: String? = null)