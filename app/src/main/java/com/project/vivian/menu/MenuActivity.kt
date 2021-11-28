package com.project.vivian.menu

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.project.vivian.MainActivity
import com.project.vivian.QRCodeActivity
import com.project.vivian.R
import com.project.vivian.VivianApp
import com.project.vivian.acerca.MapsActivity
import com.project.vivian.carrito.CarritoActivity
import com.project.vivian.cuenta.CuentaFragment
import com.project.vivian.home.HomeFragment
import com.project.vivian.productos.ProductoFragment
import com.project.vivian.reservas.MisReservacionesFragment
import com.project.vivian.reservas.ReservasFragment
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

    lateinit var toggle : ActionBarDrawerToggle
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        //val navView: NavigationView = findViewById(R.id.sidebar_view)

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
                R.id.nav_settings -> {
                    startActivity(Intent(this, MapsActivity::class.java))
                }
                R.id.nav_logout -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(R.string.dialog_signout_confirm)
                        .setCancelable(false)
                        .setPositiveButton("Si") { dialog, id ->
                            VivianApp.prefs!!.clear()
                            cerrarSesion()
                        }
                        .setNegativeButton("Cancelar") { dialog, id ->
                            dialog.dismiss()
                        }
                    val alert = builder.create()
                    alert.show()
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
                    val fragment = ProductoFragment.newInstance()
                    openFragment(fragment)
                    true
                }
                else -> false
            }
        }
        nav_view.selectedItemId = R.id.navigationHome


    }

    fun cerrarSesion(){
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_actionbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        if (item.itemId == R.id.carrito_activity){
            startActivity(Intent(this,CarritoActivity::class.java))
        }
        return false
    }

    fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_menu, fragment)
        transaction.commit()
    }

}