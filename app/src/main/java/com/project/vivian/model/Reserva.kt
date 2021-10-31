package com.project.vivian.model

import com.google.firebase.database.Exclude

data class Reserva(val nombreCliente: String, val dni: String, val fecha: String, var mesa: Mesa, val turno: String, var usuario: String? = null, @Exclude val key: String? = null)