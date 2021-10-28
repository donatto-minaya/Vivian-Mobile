package com.project.vivian.menu

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.project.vivian.MainActivity
import com.project.vivian.R
import com.project.vivian.cuenta.CuentaFragment
import com.project.vivian.home.HomeFragment
import com.project.vivian.reservas.MisReservacionesFragment
import com.project.vivian.reservas.ReservasFragment
import com.project.vivian.ui.DatePickerFragment
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.fragment_reservar.*

class MenuActivity : AppCompatActivity() {

    lateinit var toggle : ActionBarDrawerToggle
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.sidebar_view)

        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.right_sidebar_open, R.string.right_sidebar_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()

        sidebar_view.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_cuenta -> {
                    val fragment = CuentaFragment.newInstance()
                    openFragment(fragment)
                    drawerLayout.closeDrawers()
                }
                R.id.nav_reservaciones -> {
                    val fragment = MisReservacionesFragment.newInstance()
                    openFragment(fragment)
                    drawerLayout.closeDrawers()
                }
                R.id.nav_settings -> Toast.makeText(applicationContext,"Clicked configuracion",Toast.LENGTH_SHORT).show()
                R.id.nav_logout -> {
                    auth.signOut()
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            true
        }

        nav_view.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigationHome -> {
                    val fragment = HomeFragment.newInstance()
                    openFragment(fragment)
                    true
                }
                R.id.navigationReservar -> {
                    val fragment = ReservasFragment.newInstance()
                    openFragment(fragment)

                    true
                }
                R.id.navigationDelivery -> {
                    true
                }
                else -> false
            }
        }
        nav_view.selectedItemId = R.id.navigationHome


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return false
    }

    fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_menu, fragment)
        transaction.commit()
    }

}