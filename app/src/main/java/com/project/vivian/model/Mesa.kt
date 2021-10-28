package com.project.vivian.model

import com.google.firebase.database.Exclude

data class Mesa (val capacidadPersonas: Int? = 0, val disponible: Boolean? = false, @Exclude val key : String? = null)