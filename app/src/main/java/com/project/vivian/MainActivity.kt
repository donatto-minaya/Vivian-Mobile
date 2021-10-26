package com.project.vivian

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.project.vivian.menu.MenuActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_login.setOnClickListener{
            val email=login_correo.text.toString()
            val pass=login_clave.text.toString()

            if (email=="adrian" && pass=="123"){

                finish()
                startActivity(Intent(this, MenuActivity::class.java))

            }else{
                Toast.makeText(this,"Verifique sus credenciales", Toast.LENGTH_SHORT).show()
            }

        }

    }
}