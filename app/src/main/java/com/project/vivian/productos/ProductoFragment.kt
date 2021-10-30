package com.project.vivian.productos

import android.app.ProgressDialog
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.vivian.R
import com.project.vivian.cuenta.CuentaFragment
import com.project.vivian.home.HomeFragment
import com.project.vivian.model.Mesa
import com.project.vivian.model.Producto
import com.project.vivian.model.Reserva
import com.project.vivian.model.Usuario
import com.project.vivian.reservas.MisReservacionesAdapter
import com.project.vivian.reservas.MisReservacionesFragment
import kotlinx.android.synthetic.main.fragment_delivery.*
import kotlinx.android.synthetic.main.fragment_mis_reservaciones.*
import kotlinx.android.synthetic.main.item_producto.*

class ProductoFragment : Fragment() , AdapterView.OnItemSelectedListener, MisReservacionesAdapter.ItemClickListener {


    private val database = FirebaseDatabase.getInstance()
    private val myRefProducto : DatabaseReference = database.getReference("producto")
    private val myRefUsuario : DatabaseReference = database.getReference("usuario")

    var listProductos = ArrayList<Producto>();

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser : FirebaseUser
    private lateinit var progressDialog: ProgressDialog

    companion object {
        fun newInstance(): ProductoFragment = ProductoFragment()

        class MyTask(private val fragment : ProductoFragment) : AsyncTask<Void, Void, Void>(){
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
        inflater.inflate(R.layout.fragment_delivery, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        currentUser = auth.currentUser!!
        listProductos.clear()
        setupRecyclerView(recyclerProductos)
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

    private fun setupRecyclerView(recyclerView: RecyclerView){
        val listarProductosListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listProductos.clear()
                dataSnapshot.children.forEach { child ->
                    val producto: Producto? =
                            Producto(
                                child.child("imgUrl").value.toString(),
                                child.child("nombre").value.toString(),
                                child.child("precio").value.toString(),
                                child.child("stock").value.toString(),
                                child.key
                            )
                        producto?.let { listProductos.add(it) }
                }
                loadData()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        myRefProducto.addValueEventListener(listarProductosListener)
    }

    fun loadData() {
        val adapter = ProductoAdapter(listProductos,this)
        recyclerProductos?.adapter = adapter
        recyclerProductos?.layoutManager = LinearLayoutManager(context)
        progressDialog.dismiss()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemClickNoteUpdate(reservaSelected: Reserva) {
        TODO("Not yet implemented")
    }

    override fun onItemClickNoteDelete(reservaSelected: Reserva) {
        TODO("Not yet implemented")
    }

    fun openFragment(fragment: Fragment) {
        val transaction = this.requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_menu, fragment)
        transaction.commit()
    }


}