package com.project.vivian.reservas

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.graphics.Color
import android.os.AsyncTask
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
import android.widget.TextView
import com.google.firebase.database.*

import com.project.vivian.home.HomeFragment
import com.project.vivian.model.Reserva
import kotlin.collections.ArrayList


class ReservasFragment : Fragment(), AdapterView.OnItemSelectedListener {

    var listTurnos = arrayOf("Mañana","Tarde","Noche")

    private val database = FirebaseDatabase.getInstance()
    private val myRefReserva : DatabaseReference = database.getReference("reserva")
    private val myRefMesa : DatabaseReference = database.getReference("mesa")
    var mesaAgregar : Mesa = Mesa()
    var listMesasDisponibles = ArrayList<Mesa>();

    private lateinit var reservaActualizar : Reserva
    private lateinit var progressDialog: ProgressDialog
    private lateinit var idMesaFromSpinner : String

    var listCantidadPersonasSpinner : ArrayList<String> = ArrayList()
    var listIdMesaSpinner : ArrayList<String> = ArrayList()


    companion object{
        fun newInstance() : ReservasFragment = ReservasFragment()

        class MyTask(private val fragment : ReservasFragment) : AsyncTask<Void, Void, Void>(){
            override fun doInBackground(vararg p0: Void?): Void? {
                return null
            }

            override fun onPreExecute() {
                fragment.progressDialog.show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        progressDialog = ProgressDialog(this.requireActivity())
        progressDialog.progress = 10
        progressDialog.max = 100
        progressDialog.setMessage("Cargando...")
        MyTask(this).execute()
        return inflater.inflate(R.layout.fragment_reservar,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var vargs = arguments
        if (vargs != null){
            reservaActualizar = Reserva(
                vargs?.getString("nombresSend").toString(),
                vargs?.getString("apellidosSend").toString(),
                vargs?.getString("fechaSend").toString(),
                vargs?.getSerializable("mesaSend") as Mesa,
                vargs?.getString("turnoSend").toString(),
                vargs?.getString("idSend").toString()
            )
            textTitle.setText(R.string.title_reservar_modificar)
            i_nombre.setText(reservaActualizar.nombreCliente)
            i_dni.setText(reservaActualizar.dni)
            i_fecha_reservacion.setText(reservaActualizar.fecha)
            btnSolicitar.setText(R.string.btn_reservar_modificar)
            btnCancelar.setOnClickListener {
                val fragment = MisReservacionesFragment()
                openFragment(fragment)
            }
            btnSolicitar.setOnClickListener {
                val nombre = i_nombre.text.toString()
                val dni = i_dni.text.toString()
                val fecha = i_fecha_reservacion.text.toString()
                val mesa = spMesa.selectedItemId.toInt()
                val turno = listTurnos[spTurno.selectedItemPosition]

                if (nombre.isNotEmpty() && fecha.isNotEmpty() && turno.isNotEmpty() && dni.isNotEmpty()){
                    if (dni.length == 8) {
                        if (mesa != 0){
                            val reservaObj =
                                Reserva(nombre, dni, fecha, reservaActualizar.mesa, turno, reservaActualizar.key)
                            agregarOActualizarReservacion(reservaObj, true, true)
                        } else {
                            val reservaObj =
                                Reserva(nombre, dni, fecha, reservaActualizar.mesa, turno, reservaActualizar.key)
                            agregarOActualizarReservacion(reservaObj, true, false)
                        }

                    } else {
                        alertDialog("Verifique DNI")
                    }
                }else{
                    alertDialog("Verifique Datos")
                }
            }
            when(reservaActualizar.turno){
                "Mañana" -> spTurno.post(Runnable { spTurno.setSelection(0) })
                "Tarde" -> spTurno.post(Runnable { spTurno.setSelection(1) })
                else -> spTurno.post(Runnable { spTurno.setSelection(2) })
            }
        } else {
            btnCancelar.setOnClickListener {
                val fragment = HomeFragment()
                openFragment(fragment)
            }
            btnSolicitar.setOnClickListener {
                val nombre = i_nombre.text.toString()
                val dni = i_dni.text.toString()
                val fecha = i_fecha_reservacion.text.toString()
                val mesa = spMesa.selectedItemId.toInt()
                val turno = listTurnos[spTurno.selectedItemPosition]

                if (nombre.isNotEmpty() && fecha.isNotEmpty() && mesa != 0 &&turno.isNotEmpty() && dni.isNotEmpty()){
                    if (dni.length == 8){
                        val reservaObj = Reserva(nombre,dni,fecha, mesa = Mesa(),turno)
                        agregarOActualizarReservacion(reservaObj, false, false)
                    } else {
                        alertDialog("Verifique DNI")
                    }
                }else{
                    alertDialog("Verifique Datos")
                }
            }
        }


        i_fecha_reservacion.setOnClickListener {
            showDatePickerDialog()
        }

        mesasDisponibles()

        val arrayTurnosAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireActivity().baseContext,
            android.R.layout.simple_spinner_dropdown_item,
            listTurnos
        )

        spTurno.adapter = arrayTurnosAdapter

    }

    fun agregarOActualizarReservacion(reserva: Reserva, flgActualizar : Boolean, flgActualizarMesa: Boolean){

        if (flgActualizar){
            if (flgActualizarMesa){

                myRefMesa.child(reserva.mesa.key.toString()).child("disponible").setValue(true)
                myRefMesa.child(mesaAgregar.key.toString()).child("disponible").setValue(false)
                myRefMesa.child(idMesaFromSpinner).child("disponible").setValue(false)
                myRefReserva.child(reserva.key.toString()).child("dni").setValue(reserva.dni)
                myRefReserva.child(reserva.key.toString()).child("fecha").setValue(reserva.fecha)
                myRefReserva.child(reserva.key.toString()).child("nombreCliente").setValue(reserva.nombreCliente)
                myRefReserva.child(reserva.key.toString()).child("turno").setValue(reserva.turno)
                myRefReserva.child(reserva.key.toString()).child("mesa").setValue(mesaAgregar)

            } else {
                myRefReserva.child(reserva.key.toString()).child("dni").setValue(reserva.dni)
                myRefReserva.child(reserva.key.toString()).child("fecha").setValue(reserva.fecha)
                myRefReserva.child(reserva.key.toString()).child("nombreCliente").setValue(reserva.nombreCliente)
                myRefReserva.child(reserva.key.toString()).child("turno").setValue(reserva.turno)
            }

        } else {
            reserva.mesa = mesaAgregar
            myRefReserva.child(myRefReserva.push().key.toString()).setValue(reserva)
            myRefMesa.child(idMesaFromSpinner).child("disponible").setValue(false)

        }
        val fragment = MisReservacionesFragment.newInstance()
        openFragment(fragment)
    }

    fun showDatePickerDialog() {
        val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
            // +1 because January is zero
            val selectedDate = day.toString() + " / " + (month + 1) + " / " + year
            i_fecha_reservacion.setText(selectedDate)
        })
        newFragment.show(this.requireActivity().supportFragmentManager, "datePicker")
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
                view.dismiss()
            }
            .setPositiveButton("OK"){ view, id ->
                view.dismiss()
            }
            .setCancelable(false) // PARA PERMITIR QUE SE CIERRE LA ALERTA AL DAR CLICK FUERA DE ELLLA: TRUE
        dialog.show()
    }

    fun mesasDisponibles(){
        val mesasDisponiblesQuery: Query = myRefMesa.orderByChild("disponible").equalTo(true)

        val listarMesasDisponibles = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { child ->
                    val mesa: Mesa? =
                        Mesa(
                            child.child("capacidadPersonas").value.toString().toInt(),
                            child.child("disponible").value.toString().toBoolean(),
                            child.key
                        )
                    if (mesa != null) {
                        listMesasDisponibles.add(mesa)
                    }
                }
                for (i in listMesasDisponibles){
                    i.capacidadPersonas?.let { listCantidadPersonasSpinner.add(it.toString()) }
                    i.key?.let { listIdMesaSpinner.add(it.toString()) }
                }
                mesaSpinnerAdapter()
                progressDialog.dismiss()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }

        mesasDisponiblesQuery.addValueEventListener(listarMesasDisponibles)

    }

    fun mesaSpinnerAdapter(){
        listCantidadPersonasSpinner.add(0,"Cantidad de personas")
        listIdMesaSpinner.add(0,"Sin id")

        val spinnerAdapter : ArrayAdapter<String>? = context?.let { ArrayAdapter<String>(it,android.R.layout.simple_spinner_dropdown_item, listCantidadPersonasSpinner) }
        spMesa?.adapter = spinnerAdapter

        spMesa?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val value = parent!!.getItemIdAtPosition(position).toInt()
                if(value == 0){
                    (view as TextView).setTextColor(Color.GRAY)
                }
                idMesaFromSpinner = listIdMesaSpinner[position]
                for (i in listMesasDisponibles){
                    if (i.key == idMesaFromSpinner){
                        mesaAgregar = i
                    }
                }
            }

        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}
