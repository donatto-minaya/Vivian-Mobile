package com.project.vivian.model

import com.google.firebase.database.Exclude

data class Usuario(var dni: String? = null, var nombres: String? = null, var apellidos: String? = null, var telefono: String? = null, @Exclude var key: String? = null)