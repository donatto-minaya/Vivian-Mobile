package com.project.vivian.model

import com.google.firebase.database.Exclude
import java.io.Serializable

data class Mesa (val capacidadPersonas: Int? = 0, val disponible: Boolean? = false, @Exclude val key : String? = null) : Serializable