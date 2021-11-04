package com.project.vivian.model

import com.google.firebase.database.Exclude

data class ItemCarrito(var producto :  Producto? = null, var cantidad : Int? = null, var preciototal: Double? = null, val usuario : String? = null, @Exclude val key : String? = null )