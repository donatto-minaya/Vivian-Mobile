package com.project.vivian.model

import com.google.firebase.database.Exclude

data class Producto(val imgUrl: String ? = null, val nombre: String ? = null, val precio: String ? = null, val stock: String ? = null, @Exclude val key : String ? = null)