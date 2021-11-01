package com.project.vivian.cuenta

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.vivian.MainActivity
import com.project.vivian.R
import com.project.vivian.model.Usuario
import kotlinx.android.synthetic.main.fragment_cuenta.*
import java.util.regex.Pattern


class CuentaFragment: Fragment(), AdapterView.OnItemSelectedListener {

    private val database = FirebaseDatabase.getInstance()
    private val myRef : DatabaseReference = database.getReference("usuario")

    private lateinit var progressDialog: ProgressDialog
    private lateinit var progressDialog2: ProgressDialog
    private lateinit var progressDialog3: ProgressDialog
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser : FirebaseUser
    private lateinit var usuarioEncontrado : Usuario

    private val fileResult = 1


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

        progressDialog3 = ProgressDialog(this.requireContext())
        progressDialog3.setMessage("Cargando...")

        auth = Firebase.auth
        usuarioEncontrado = Usuario()
        updateUI()
        currentUser = auth.currentUser!!

        btn_actualizar_profile.setOnClickListener {
            val builder = AlertDialog.Builder(this.requireContext())
            builder.setMessage(R.string.dialog_updateprofile_confirm)
                .setCancelable(false)
                .setPositiveButton("Si") { dialog, id ->
                    updateProfile()
                }
                .setNegativeButton("Cancelar") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()

        }

        profileImageView.setOnClickListener {
            fileManager()
        }

        signOutImageView.setOnClickListener {
            val builder = AlertDialog.Builder(this.requireContext())
            builder.setMessage(R.string.dialog_signout_confirm)
                .setCancelable(false)
                .setPositiveButton("Si") { dialog, id ->
                    auth.signOut()
                    startActivity(Intent(this.requireActivity(), MainActivity::class.java))
                }
                .setNegativeButton("Cancelar") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()

        }

        textCambiarClave.setOnClickListener {
            val fragment = CambiarClaveFragment.newInstance()
            openFragment(fragment)
        }

    }

    private  fun updateProfile() {
        val dniRegex = Pattern.compile("^(^[0-9]*\$)")
        val nombresApellidosRegex = Pattern.compile("^[A-Za-z\\s]+\$")
        val nombres = nameEditText.text.toString()
        val dni = dniEditText.text.toString()
        val apellidos = apellidosEditText.text.toString()
        val telefono = telefonoEditText.text.toString()

        if (nombres.isNotEmpty() && dni.isNotEmpty() && apellidos.isNotEmpty()){
            if (dniRegex.matcher(dni).matches() && dni.length == 8){
                if (nombresApellidosRegex.matcher(nombres).matches()){
                    if (nombresApellidosRegex.matcher(apellidos).matches()){
                        if (telefono.isNotEmpty()){
                            if (dniRegex.matcher(telefono).matches()){
                                progressDialog3.show()
                                myRef.child(currentUser.uid).child("apellidos").setValue(apellidos)
                                myRef.child(currentUser.uid).child("dni").setValue(dni)
                                myRef.child(currentUser.uid).child("nombres").setValue(nombres)
                                myRef.child(currentUser.uid).child("telefono").setValue(telefono)
                                Toast.makeText(this.requireContext(),"Perfil actualizado correctamente.",Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this.requireContext(),"Ingresa un Teléfono válido",Toast.LENGTH_LONG).show()
                            }
                        } else {
                            progressDialog3.show()
                            myRef.child(currentUser.uid).child("apellidos").setValue(apellidos)
                            myRef.child(currentUser.uid).child("dni").setValue(dni)
                            myRef.child(currentUser.uid).child("nombres").setValue(nombres)
                            myRef.child(currentUser.uid).child("telefono").setValue(telefono)
                            Toast.makeText(this.requireContext(),"Perfil actualizado correctamente.",Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this.requireContext(),"Ingresa Apellidos válidos",Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this.requireContext(),"Ingresa Nombres válidos",Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this.requireContext(),"Ingresa un DNI válido",Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this.requireContext(),"Lo sentimos... DNI, Nombres y Apellidos obligatorios.",Toast.LENGTH_LONG).show()
        }
    }

    fun updateUI(){
        currentUser = auth.currentUser!!
        val emailTv = emailCuentaTextView
        val nombresTv = nameEditText
        val apellidosTv = apellidosEditText
        val dniTv = dniEditText
        val teleTv = telefonoEditText
        val nomCompletoTv = nombreCompletoTextView

        val usuarioListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val myObject : Usuario? = dataSnapshot.child(currentUser.uid).getValue(Usuario::class.java)
                if (myObject != null) {
                    myObject.key = currentUser.uid
                    emailTv.setText(currentUser.email)
                    nombresTv.setText(myObject.nombres)
                    apellidosTv.setText(myObject.apellidos)
                    dniTv.setText(myObject.dni)
                    teleTv.setText(myObject.telefono)
                    nomCompletoTv.setText(myObject.nombres + " "+ myObject.apellidos)
                }
                progressDialog.dismiss()
                if (progressDialog3.isShowing){
                    progressDialog3.dismiss()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        }
        myRef.addValueEventListener(usuarioListener)
        loadFoto()
    }

    private fun fileManager() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, fileResult)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == fileResult) {
            if (resultCode == RESULT_OK && data != null) {
                val uri = data.data
                progressDialog2 = ProgressDialog(this.requireContext())
                progressDialog2.setMessage("Cargando...")
                progressDialog2.show()
                uri?.let { imageUpload(it) }

            }
        }
    }

    private fun imageUpload(mUri: Uri) {

        val user = auth.currentUser
        val folder: StorageReference = FirebaseStorage.getInstance().reference.child("usuario")
        val fileName: StorageReference = folder.child("img"+user!!.uid)

        fileName.putFile(mUri).addOnSuccessListener {
            fileName.downloadUrl.addOnSuccessListener { uri ->

                val profileUpdates = userProfileChangeRequest {
                    photoUri = Uri.parse(uri.toString())
                }

                user.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            progressDialog2.dismiss()
                            Toast.makeText(this.requireActivity(), "Foto subida correctamente.", Toast.LENGTH_SHORT).show()
                            updateUI()
                        }
                    }
            }
        }.addOnFailureListener {
            Log.i("TAG", "file upload error")
        }
    }

    fun loadFoto(){
        Glide
            .with(this.requireActivity())
            .load(currentUser.photoUrl)
            .centerCrop()
            .placeholder(R.drawable.anonymous_profile)
            .error(R.drawable.anonymous_profile)
            .into(profileImageView)
    }

    fun openFragment(fragment: Fragment) {
        val transaction = this.requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_menu, fragment)
        transaction.commit()
    }

}