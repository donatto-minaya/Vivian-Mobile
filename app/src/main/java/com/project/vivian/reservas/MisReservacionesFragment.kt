package com.project.vivian.reservas

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.project.vivian.R
import com.project.vivian.model.Reserva
import kotlinx.android.synthetic.main.fragment_mis_reservaciones.*

class MisReservacionesFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val database = FirebaseDatabase.getInstance()
    private val myRef : DatabaseReference = database.getReference("reserva")
    var listReservas = ArrayList<Reserva>();

    companion object{
        fun newInstance() : MisReservacionesFragment = MisReservacionesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_mis_reservaciones, container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myRef.addValueEventListener(reservaListener)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


    val reservaListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            listReservas.clear()
            dataSnapshot.children.forEach { child ->
                val reserva: Reserva =
                    Reserva(
                        child.child("nombreCliente").value.toString(),
                        child.child("dni").value.toString(),
                        child.child("fecha").value.toString(),
                        child.child("mesa").value.toString().toInt(),
                        child.child("turno").value.toString(),
                        child.key)
                reserva.let { listReservas.add(it) }
            }
            recyclerReservas.apply {
                layoutManager = GridLayoutManager(context,2)
                adapter = MisReservacionesAdapter(listReservas)
            }
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
            Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
        }
    }

    /*fun loadData(){
        recyclerReservas.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = MisReservacionesAdapter(listReservas)
        }
    }*/

}