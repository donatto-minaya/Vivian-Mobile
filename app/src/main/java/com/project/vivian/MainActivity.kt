package com.project.vivian

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.project.vivian.menu.MenuActivity
import com.project.vivian.model.Mesa
import com.project.vivian.model.Producto
import com.project.vivian.model.Usuario
import com.project.vivian.reservas.MisReservacionesFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    //private val database = FirebaseDatabase.getInstance()
    //private val myRef : DatabaseReference = database.getReference("usuario")
    //private val myRef2 : DatabaseReference = database.getReference("mesa")

    companion object{
        class MyTask(private val activity: MainActivity ) : AsyncTask<Void, Void, Void>(){
            override fun doInBackground(vararg p0: Void?): Void? {
                return null
            }

            override fun onPreExecute() {
                activity.progressDialog.show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth


        progressDialog = ProgressDialog(this)
        progressDialog.progress = 10
        progressDialog.max = 100
        progressDialog.setMessage("Cargando...")

        /*val usu = Usuario("asdasd","Ramen","14.99","5")
        myRef.child("sfStWtGWWZheW9fryYoUePs0U0T2").setValue(usu)
        var mesa = Mesa(3,true)
        myRef2.child(myRef2.push().key.toString()).setValue(mesa)*/

        button_login.setOnClickListener{
            val email=login_correo.text.toString()
            val pass=login_clave.text.toString()

            when{
                email.isEmpty() || pass.isEmpty() -> {
                    Toast.makeText(baseContext, "Ingrese datos para Iniciar Sesion.", Toast.LENGTH_SHORT).show()
                } else ->{
                    progressDialog.show()
                    signIn(email, pass)
                }
            }
        }

        button_registrar.setOnClickListener {
            startActivity(Intent(this,RegistroActivity::class.java))
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload();
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    reload()
                } else {
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Correo o contraseña incorrectos.", Toast.LENGTH_SHORT).show()
                }
                progressDialog.dismiss()
            }
    }

    private fun reload(){
        progressDialog.dismiss()
        finish()
        startActivity(Intent(this,MenuActivity::class.java))
    }

}