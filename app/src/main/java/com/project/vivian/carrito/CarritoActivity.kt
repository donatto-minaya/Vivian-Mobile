package com.project.vivian.carrito

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.project.vivian.R
import com.project.vivian.model.ItemCarrito
import com.project.vivian.model.Mesa
import com.project.vivian.model.Producto
import com.project.vivian.model.Reserva
import kotlinx.android.synthetic.main.activity_carrito_compras.*
import kotlinx.android.synthetic.main.fragment_delivery.*

class CarritoActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val myRefCarrito : DatabaseReference = database.getReference("carrito")

    var listItemsCarrito = ArrayList<ItemCarrito>()

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser : FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito_compras)

        auth = Firebase.auth
        currentUser = auth.currentUser!!
        listItemsCarrito.clear()
        setupRecyclerView(recyclerCarrito)

        carrito_goback.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView){
        val itemsPorEmailQuery: Query = myRefCarrito.orderByChild("usuario").equalTo(currentUser.email)

        val listarItemCarritoListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listItemsCarrito.clear()
                dataSnapshot.children.forEach { child ->
                    val itemCarrito: ItemCarrito? =
                        child.child("producto").getValue(Producto::class.java)?.let {
                            ItemCarrito(
                                it,
                                child.child("cantidad").value.toString().toInt(),
                                child.child("preciototal").value.toString().toDouble(),
                                child.child("usuario").value.toString(),
                                child.key
                            )
                        }
                    itemCarrito?.let { listItemsCarrito.add(it) }
                }
                var precioTotal : Double = 0.0
                if (listItemsCarrito.size == 0){
                    Log.v("TAMANIO",listItemsCarrito.size.toString())
                    lista_carrito_vacia.visibility = View.VISIBLE
                    button_confirmar_carrito.visibility = View.GONE
                    precio_total_carrito.visibility = View.GONE
                    title_precio_total.visibility = View.GONE
                } else {
                    lista_carrito_vacia.visibility = View.GONE
                    button_confirmar_carrito.visibility = View.VISIBLE
                    precio_total_carrito.visibility = View.VISIBLE
                    title_precio_total.visibility = View.VISIBLE
                    for (i in listItemsCarrito){
                        precioTotal+= i.preciototal!!
                    }
                    precio_total_carrito.text = precioTotal.toString()
                }
                loadData(listItemsCarrito)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        itemsPorEmailQuery.addValueEventListener(listarItemCarritoListener)
    }


    fun loadData(listItemsCarrito : ArrayList<ItemCarrito>) {

        recyclerCarrito.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CarritoAdapter(listItemsCarrito)
            val swipeDelete = object : SwipeToDeleteCallBack(context) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    deleteItem(viewHolder.adapterPosition)
                }

            }
            val touchHelper = ItemTouchHelper(swipeDelete)
            touchHelper.attachToRecyclerView(recyclerCarrito)
        }

    }

    fun deleteItem(position: Int){
        myRefCarrito.child(listItemsCarrito[position].key.toString()).removeValue()
    }
}