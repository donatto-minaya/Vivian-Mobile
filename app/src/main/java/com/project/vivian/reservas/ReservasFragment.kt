package com.project.vivian.reservas

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.project.vivian.R
import com.project.vivian.model.Mesa
import com.project.vivian.ui.DatePickerFragment
import kotlinx.android.synthetic.main.fragment_reservar.*
import android.widget.ArrayAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot
import com.project.vivian.home.HomeFragment
import com.project.vivian.model.Reserva
import java.util.*
import kotlin.collections.ArrayList


class ReservasFragment : Fragment(), AdapterView.OnItemSelectedListener {

    var listMesas = arrayOf(1,2,3,4,5)
    var listTurnos = arrayOf("Mañana","Tarde","Noche")

    private val database = FirebaseDatabase.getInstance()
    private val myRef : DatabaseReference = database.getReference("reserva")

    companion object{
        fun newInstance() : ReservasFragment = ReservasFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_reservar,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        i_fecha_reservacion.setOnClickListener {
            showDatePickerDialog()
        }

        val arrayMesasAdapter: ArrayAdapter<Int> = ArrayAdapter<Int>(
            requireActivity().baseContext,
            android.R.layout.simple_spinner_dropdown_item,
            listMesas
        )

        val arrayTurnosAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireActivity().baseContext,
            android.R.layout.simple_spinner_dropdown_item,
            listTurnos
        )

        spMesa.adapter = arrayMesasAdapter
        spTurno.adapter = arrayTurnosAdapter


        val nReservas = listOf(
            Reserva("Adrian Arcelles","12345678","20/10/2021",1,"Mañana"),
            Reserva("Cristobal Colon","76575856","01/05/2021",2,"Tarde"),
            Reserva("Adolf Hitler","34578456","07/12/2021",3,"Noche"),
            Reserva("Juan Vargas","78568457","04/01/2021",4,"Mañana")
        )

        // INGRESÉ RESERVAS DE PRUEBA
        /*for (reserva in nReservas){
            myRef.child(myRef.push().key.toString()).setValue(reserva)
        }*/

        /*myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val mesas = dataSnapshot.value as ArrayList<*>
                for (mesa in mesas) {
                    Log.d("PRUEBA", "Value is: $mesa")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("PRUEBA", "Failed to read value.", error.toException())
            }
        })*/

        btnSolicitar.setOnClickListener {
            val nombre = i_nombre.text.toString()
            val fecha = i_fecha_reservacion.text.toString()
            val mesa = listMesas[spMesa.selectedItemPosition].toString()
            val turno = listTurnos[spTurno.selectedItemPosition]
            val dni = i_dni.text.toString()

            Log.v("FORMULARIO_CURSO",nombre)
            Log.v("FORMULARIO_CURSO",dni)
            Log.v("FORMULARIO_CURSO",fecha)
            Log.v("FORMULARIO_CURSO",mesa)
            Log.v("FORMULARIO_CURSO",turno)

            if (nombre.isNotEmpty() && fecha.isNotEmpty() && mesa.isNotEmpty() && turno.isNotEmpty()){
                val fragment = MisReservacionesFragment.newInstance()
                openFragment(fragment)
            }else{
                alertDialog("Verifique Datos")
            }
        }
    }

    fun agregarReservacion(){

    }

    fun showDatePickerDialog() {
        val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
            // +1 because January is zero
            val selectedDate = day.toString() + " / " + (month + 1) + " / " + year
            i_fecha_reservacion.setText(selectedDate)
        })
        newFragment.show(this.requireActivity().supportFragmentManager, "datePicker")
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    fun openFragment(fragment: Fragment) {
        val transaction = this.requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_menu, fragment)
        transaction.commit()
    }

    fun alertDialog(message: String){
        val dialog = AlertDialog.Builder(this.requireActivity())
            .setIcon(R.drawable.confirmation)
            .setTitle(R.string.dialog_title)
            .setMessage(message)
            .setNegativeButton("Cancelar"){ view, id ->
                Log.v("ACTION_BUTTON",id.toString())
                view.dismiss()
            }
            .setPositiveButton("Aceptar"){ view, id ->
                Log.v("ACTION_BUTTON",id.toString())
                view.dismiss()
            }
            .setCancelable(false) // PARA PERMITIR QUE SE CIERRE LA ALERTA AL DAR CLICK FUERA DE ELLLA: TRUE
        dialog.show()
    }

}
