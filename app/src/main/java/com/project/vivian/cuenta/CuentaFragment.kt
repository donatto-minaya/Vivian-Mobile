package com.project.vivian.cuenta

import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.project.vivian.R
import com.project.vivian.databinding.ActivityMainBinding
import com.project.vivian.databinding.FragmentCuentaBinding
import com.project.vivian.model.Reserva
import com.project.vivian.model.Usuario
import com.project.vivian.reservas.MisReservacionesAdapter
import com.project.vivian.reservas.MisReservacionesFragment
import kotlinx.android.synthetic.main.fragment_cuenta.*
import kotlinx.android.synthetic.main.fragment_mis_reservaciones.*




class CuentaFragment: Fragment(), AdapterView.OnItemSelectedListener {

    private val database = FirebaseDatabase.getInstance()
    private val myRef : DatabaseReference = database.getReference("usuario")

    private lateinit var progressDialog: ProgressDialog
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser : FirebaseUser
    private lateinit var usuarioEncontrado : Usuario


    companion object{
        fun newInstance() : CuentaFragment = CuentaFragment()

        class MyTask(private val fragment : CuentaFragment) : AsyncTask<Void, Void, Void>(){
            override fun doInBackground(vararg p0: Void?): Void? {
                return null
            }

            override fun onPreExecute() {
                fragment.progressDialog.show()
            }
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressDialog = ProgressDialog(this.requireActivity())
        progressDialog.progress = 10
        progressDialog.max = 100
        progressDialog.setMessage("Cargando...")
        MyTask(this).execute()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_cuenta, container,false)



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        usuarioEncontrado = Usuario()
        updateUI()

    }

    override fun onStart() {
        super.onStart()
        currentUser = auth.currentUser!!
    }

    fun updateUI(){
        val usuarioListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val myObject : Usuario? = dataSnapshot.child(currentUser.uid).getValue(Usuario::class.java)
                if (myObject != null) {
                    myObject.key = currentUser.uid
                    nameEditText.setText(myObject.nombres)
                    apellidosEditText.setText(myObject.apellidos)
                    dniEditText.setText(myObject.dni)
                    telefonoEditText.setText(myObject.telefono)
                    nombreCompletoTextView.setText(myObject.nombres + " "+ myObject.apellidos)
                }
                progressDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        myRef.addValueEventListener(usuarioListener)
    }

}