package com.project.vivian.cuenta

import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.project.vivian.R
import kotlinx.android.synthetic.main.fragment_cambiar_clave.*
import java.util.regex.Pattern

class CambiarClaveFragment : Fragment() {

    private lateinit var progressDialog: ProgressDialog
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser : FirebaseUser

    companion object{
        fun newInstance() : CambiarClaveFragment = CambiarClaveFragment()

        class MyTask(private val fragment : CambiarClaveFragment) : AsyncTask<Void, Void, Void>(){
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
    ): View? =
        inflater.inflate(R.layout.fragment_cambiar_clave, container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        progressDialog = ProgressDialog(this.requireActivity())
        progressDialog.progress = 10
        progressDialog.max = 100
        progressDialog.setMessage("Cargando...")

        val passwordRegex = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,20}$")

        button_cambiarclave_guardar.setOnClickListener {
            val currentPassword = textClaveActual.text.toString()
            val newPassword = textNuevaClave.text.toString()
            val repeatPassword = textNuevaClaveRepeat.text.toString()

            if (newPassword.isEmpty() || !passwordRegex.matcher(newPassword).matches()){
                Toast.makeText(this.requireContext(), "Contreseña débil. Al menos un caracter Mayúscula, uno Minúscula, un dígito y 6 caracteres", Toast.LENGTH_LONG).show()
            } else if (newPassword != repeatPassword){
                Toast.makeText(this.requireContext(), "Las contraseñas no coinciden.", Toast.LENGTH_LONG).show()
            } else {
                progressDialog.show()
                changePassword(currentPassword, newPassword)
            }
        }

        cambiarclave_goback.setOnClickListener {
            val fragment = CuentaFragment.newInstance()
            openFragment(fragment)
        }
    }

    private  fun changePassword(current : String, password : String){
        val user = auth.currentUser

        if (user != null){
            val email = user.email
            val credential = EmailAuthProvider
                .getCredential(email!!, current)

            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    progressDialog.dismiss()
                    if(task.isSuccessful) {
                        user.updatePassword(password)
                            .addOnCompleteListener { taskUpdatePassword ->
                                if (taskUpdatePassword.isSuccessful) {
                                    val fragment = CuentaFragment.newInstance()
                                    openFragment(fragment)
                                    Toast.makeText(this.requireContext(), "Se actualizó tu clave correctamente.", Toast.LENGTH_LONG).show()
                                }
                            }

                    } else {
                        Toast.makeText(this.requireContext(), "La contraseña actual es incorrecta.", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    fun openFragment(fragment: Fragment) {
        val transaction = this.requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_menu, fragment)
        transaction.commit()
    }

}