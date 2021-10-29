package com.project.vivian

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.project.vivian.cuenta.CuentaFragment
import com.project.vivian.menu.MenuActivity
import kotlinx.android.synthetic.main.activity_registro.*
import java.util.regex.Pattern

class RegistroActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef : DatabaseReference = database.getReference("usuario")

    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    companion object{

        class MyTask(private val activity: RegistroActivity) : AsyncTask<Void, Void, Void>(){
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
        setContentView(R.layout.activity_registro)

        auth = Firebase.auth

        val passwordRegex = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,20}$")

        //(?=.*[0-9]) # a digit must occur at least once
        //(?=.*[a-z]) # a lower case letter must occur at least once
        //(?=.*[A-Z]) # an upper case letter must occur at least once
        //(?=.[-@%[}+'!/#$^?:;,(")~`.=&{>]<_]) # a special character must occur at least once replace with your special characters
        //(?=\S+$) # no whitespace allowed in the entire string .{8,} # anything, at least six places though

        goBack.setOnClickListener {
            finish()
        }

        button_registro_registrar.setOnClickListener {
            val mEmail = edtRegistroCorreo.text.toString()
            val mPassword = edtRegistroClave.text.toString()
            val mRepeatPassword = edtRegistroClaveRepeat.text.toString()

            if(mEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
                Toast.makeText(this, "Ingrese un email válido",
                    Toast.LENGTH_SHORT).show()
            } else if (mPassword.isEmpty() || !passwordRegex.matcher(mPassword).matches()){
                Toast.makeText(this, "Contreseña débil. Al menos un caracter Mayúscula, uno Minúscula y un dígito",
                    Toast.LENGTH_LONG).show()
            } else if (mPassword != mRepeatPassword){
                Toast.makeText(this, "Las contraseñas no coinciden",
                    Toast.LENGTH_SHORT).show()
            } else {
                progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Cargando...")
                progressDialog.show()
                createAccount(mEmail, mPassword)
            }
        }

        button_cancelar.setOnClickListener {
            finish()
        }
    }


    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    myRef.child(task.result?.user?.uid.toString()).child("apellidos").setValue("")
                    myRef.child(task.result?.user?.uid.toString()).child("nombres").setValue("")
                    myRef.child(task.result?.user?.uid.toString()).child("dni").setValue("")
                    myRef.child(task.result?.user?.uid.toString()).child("telefono").setValue("")
                    progressDialog.dismiss()
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(R.string.dialong_creado_correctamente)
                        .setCancelable(false)
                        .setPositiveButton("OK") { dialog, id ->
                            finish()
                            startActivity(Intent(this,MenuActivity::class.java))
                        }
                    val alert = builder.create()
                    alert.show()
                } else {
                    Toast.makeText(this, "No se pudo crear la cuenta. Vuelva a intentarlo",
                        Toast.LENGTH_SHORT).show()
                }
            }

    }





}