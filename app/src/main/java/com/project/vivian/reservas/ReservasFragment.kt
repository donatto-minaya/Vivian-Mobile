package com.project.vivian.reservas

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
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
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

import com.project.vivian.home.HomeFragment
import com.project.vivian.model.Reserva
import kotlinx.android.synthetic.main.item_reserva.*
import java.util.*
import kotlin.collections.ArrayList





class ReservasFragment : Fragment(), AdapterView.OnItemSelectedListener {

    var listTurnos = arrayOf("Mañana","Tarde","Noche")

    var nombreCliente : String = ""
    var dni : String = ""
    var fecha : String = ""
    var mesa : String = ""
    var turno : String = ""
    var idActualizar : Int = 0

    /*args.putString("nombresSend",reservaSelected.nombreCliente)
    args.putString("apellidosSend",reservaSelected.dni)
    args.putString("fechaSend",reservaSelected.fecha)
    args.putString("mesaSend",reservaSelected.mesa.toString())
    args.putString("turnoSend",reservaSelected.turno)
    args.putString("idSend",reservaSelected.key)*/

    private val database = FirebaseDatabase.getInstance()
    private val myRefReserva : DatabaseReference = database.getReference("reserva")
    private val myRefMesa : DatabaseReference = database.getReference("mesa")


    var listMesasDisponibles = ArrayList<Mesa>();

    private lateinit var reservaActualizar : Reserva
    private lateinit var progressDialog: ProgressDialog
    var listMesasSpinner : ArrayList<Int> = ArrayList()

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
                vargs?.getString("mesaSend").toString().toInt(),
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
            when(reservaActualizar.turno){
                "Mañana" -> spTurno.post(Runnable { spTurno.setSelection(0) })
                "Tarde" -> spTurno.post(Runnable { spTurno.setSelection(1) })
                else -> spTurno.post(Runnable { spTurno.setSelection(2) })
            }
        }


        i_fecha_reservacion.setOnClickListener {
            showDatePickerDialog()
        }

        /*val arrayMesasAdapter: ArrayAdapter<Int> = ArrayAdapter<Int>(
            requireActivity().baseContext,
            android.R.layout.simple_spinner_dropdown_item,
            listMesas
        )*/
        mesasDisponibles()

        val arrayTurnosAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireActivity().baseContext,
            android.R.layout.simple_spinner_dropdown_item,
            listTurnos
        )

        spTurno.adapter = arrayTurnosAdapter

        btnSolicitar.setOnClickListener {
            val nombre = i_nombre.text.toString()
            val dni = i_dni.text.toString()
            val fecha = i_fecha_reservacion.text.toString()
            val mesa = listMesasSpinner[spMesa.selectedItemPosition]
            val turno = listTurnos[spTurno.selectedItemPosition]

            if (nombre.isNotEmpty() && fecha.isNotEmpty() && turno.isNotEmpty()){
                val reservaObj = Reserva(nombre,dni,fecha,mesa.toInt(),turno)
                agregarReservacion(reservaObj)
                val fragment = MisReservacionesFragment.newInstance()
                openFragment(fragment)
            }else{
                alertDialog("Verifique Datos")
            }
        }
    }

    fun agregarReservacion(reserva: Reserva){
        myRefReserva.child(myRefReserva.push().key.toString()).setValue(reserva)
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
                    i.capacidadPersonas?.let { listMesasSpinner.add(it) }
                }
                val arrayMesasAdapter: ArrayAdapter<Int> = ArrayAdapter<Int>(
                    requireActivity().baseContext,
                    android.R.layout.simple_spinner_dropdown_item,
                    listMesasSpinner
                )
                spMesa?.adapter = arrayMesasAdapter
                progressDialog.dismiss()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }

        mesasDisponiblesQuery.addValueEventListener(listarMesasDisponibles)

    }

}
