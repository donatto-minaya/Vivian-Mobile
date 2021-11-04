package com.project.vivian.reservas

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.graphics.Bitmap
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
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.zxing.WriterException
import com.project.vivian.cuenta.CuentaFragment
import com.project.vivian.home.HomeFragment
import com.project.vivian.model.Reserva
import com.project.vivian.model.Usuario
import java.io.ByteArrayOutputStream
import kotlin.collections.ArrayList
import android.net.Uri
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.regex.Pattern


class ReservasFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val database = FirebaseDatabase.getInstance()
    private val myRefReserva : DatabaseReference = database.getReference("reserva")
    private val myRefMesa : DatabaseReference = database.getReference("mesa")
    private val myRefUsuario : DatabaseReference = database.getReference("usuario")

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser : FirebaseUser
    private lateinit var reservaActualizar : Reserva
    private lateinit var progressDialog: ProgressDialog
    private lateinit var progressDialogReservar : ProgressDialog
    private lateinit var idMesaFromSpinner : String

    var listCantidadPersonasSpinner : ArrayList<String> = ArrayList()
    var listIdMesaSpinner : ArrayList<String> = ArrayList()
    var mesaAgregar : Mesa = Mesa()
    var listMesasDisponibles = ArrayList<Mesa>();
    var listTurnos = arrayOf("Mañana","Tarde","Noche")
    var bitmap: Bitmap? = null
    var qrgEncoder: QRGEncoder? = null
    var downloadUrl: Uri? = null

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

    override fun onStart() {
        super.onStart()
        validarUsuario()
    }

    fun validarUsuario(){
        val usuarioListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val myObject : Usuario? = dataSnapshot.child(currentUser.uid).getValue(Usuario::class.java)
                if (myObject != null) {
                    if (myObject.nombres != ""){
                        return
                    } else {
                        if (context != null){
                            val builder = android.app.AlertDialog.Builder(context)
                            builder.setMessage(R.string.dialog_actualice_datos)
                                .setCancelable(false)
                                .setPositiveButton("Actualizar Ahora") { dialog, id ->
                                    val fragment = CuentaFragment.newInstance()
                                    openFragment(fragment)
                                }
                                .setNegativeButton("Cancelar"){ dialog, id ->
                                    val fragment = HomeFragment.newInstance()
                                    openFragment(fragment)
                                }
                            val alert = builder.create()
                            alert.show()
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        myRefUsuario.addValueEventListener(usuarioListener)
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
        auth = Firebase.auth
        currentUser = auth.currentUser!!

        progressDialogReservar = ProgressDialog(this.requireActivity())
        progressDialogReservar.progress = 10
        progressDialogReservar.max = 100
        progressDialogReservar.setMessage("Reservando...")

        val nombresApellidosRegex = Pattern.compile("^[A-Za-z\\s]+\$")

        var vargs = arguments
        if (vargs != null){
            imgQrCode.visibility = View.VISIBLE

            reservaActualizar = Reserva(
                vargs?.getString("nombresSend").toString(),
                vargs?.getString("apellidosSend").toString(),
                vargs?.getString("fechaSend").toString(),
                vargs?.getSerializable("mesaSend") as Mesa,
                vargs?.getString("turnoSend").toString(),
                vargs?.getString("usuarioSend").toString(),
                vargs?.getString("qrCodeUrlSend").toString(),
                vargs?.getString("idSend").toString()
            )
            val options = RequestOptions()
                .placeholder(R.drawable.anonymous_image)
                .error(R.drawable.ic_launcher_background)
            imgQrCode?.let {
                Glide.with(it)
                    .setDefaultRequestOptions(options)
                    .load(reservaActualizar.qrcodeUrl)
                    .into(it)
            }
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
                        if (nombresApellidosRegex.matcher(nombre).matches()){
                            progressDialogReservar.show()
                            if (mesa != 0){
                                val reservaObj =
                                    Reserva(nombre, dni, fecha, reservaActualizar.mesa, turno,currentUser.email.toString(),reservaActualizar.qrcodeUrl, reservaActualizar.key)
                                agregarOActualizarReservacion(reservaObj, true, true)
                            } else {
                                val reservaObj =
                                    Reserva(nombre, dni, fecha, reservaActualizar.mesa, turno,currentUser.email.toString(),reservaActualizar.qrcodeUrl, reservaActualizar.key)
                                agregarOActualizarReservacion(reservaObj, true, false)
                            }
                        } else {
                            Toast.makeText(this.requireContext(),"Ingresa un Nombre válido", Toast.LENGTH_LONG).show()
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
            imgQrCode.visibility = View.GONE

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
                        if (nombresApellidosRegex.matcher(nombre).matches()){
                            progressDialogReservar.show()
                            val reservaObj = Reserva(nombre,dni,fecha, mesa = Mesa(),turno,currentUser.email.toString(),"")
                            agregarOActualizarReservacion(reservaObj, false, false)
                        } else {
                            Toast.makeText(this.requireContext(),"Ingresa un Nombre válido", Toast.LENGTH_LONG).show()
                        }
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
                progressDialogReservar.dismiss()

            } else {
                myRefReserva.child(reserva.key.toString()).child("dni").setValue(reserva.dni)
                myRefReserva.child(reserva.key.toString()).child("fecha").setValue(reserva.fecha)
                myRefReserva.child(reserva.key.toString()).child("nombreCliente").setValue(reserva.nombreCliente)
                myRefReserva.child(reserva.key.toString()).child("turno").setValue(reserva.turno)
                progressDialogReservar.dismiss()
            }
            val fragment = MisReservacionesFragment.newInstance()
            openFragment(fragment)
        } else {
            reserva.mesa = mesaAgregar
            val agregarNuevaReservaListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    setUrlQrCodeReserva(dataSnapshot.key.toString(), reserva)
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            myRefReserva.child(myRefReserva.push().key.toString()).addListenerForSingleValueEvent(agregarNuevaReservaListener)
            myRefMesa.child(idMesaFromSpinner).child("disponible").setValue(false)

        }

    }

    fun setUrlQrCodeReserva(key: String, reserva : Reserva): Uri? {
        qrgEncoder =
            QRGEncoder(key, null, QRGContents.Type.TEXT,500)
        try {
            bitmap = qrgEncoder!!.encodeAsBitmap()
            var baos : ByteArrayOutputStream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG,90,baos)
            var data : ByteArray = baos.toByteArray()
            val folder: StorageReference = FirebaseStorage.getInstance().reference.child("qrcode")
            val fileName: StorageReference = folder.child("reserva$key")

            val uploadTask: UploadTask = fileName.putBytes(data)
            uploadTask.addOnFailureListener {
            }
            .addOnSuccessListener { taskSnapshot ->
                downloadUrl = taskSnapshot.uploadSessionUri
                myRefReserva.child(key).setValue(reserva)

                fileName.getDownloadUrl().addOnSuccessListener { uri ->
                    myRefReserva.child(key).child("qrcodeUrl").setValue(uri.toString())
                }
                progressDialogReservar.dismiss()
                val fragment = MisReservacionesFragment.newInstance()
                openFragment(fragment)
            }
            return downloadUrl
        } catch (e: WriterException) {
            Log.v("Tag", e.toString())
            return null
        }
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
