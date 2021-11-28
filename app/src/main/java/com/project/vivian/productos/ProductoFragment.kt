package com.project.vivian.productos

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.project.vivian.R
import com.project.vivian.cuenta.CuentaFragment
import com.project.vivian.home.HomeFragment
import com.project.vivian.model.Producto
import com.project.vivian.model.Usuario
import kotlinx.android.synthetic.main.dialog_producto.view.*
import kotlinx.android.synthetic.main.fragment_delivery.*
import kotlinx.android.synthetic.main.item_producto.view.*
import kotlin.math.round

class ProductoFragment : Fragment() , AdapterView.OnItemSelectedListener, ProductoAdapter.ItemClickListener {


    private val database = FirebaseDatabase.getInstance()
    private val myRefProducto : DatabaseReference = database.getReference("producto")
    private val myRefUsuario : DatabaseReference = database.getReference("usuario")
    private val myRefCarrito : DatabaseReference = database.getReference("carrito")

    var listProductos = ArrayList<Producto>()

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
                    if (myObject.dni != ""){
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
                                child.child("descripcion").value.toString(),
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

    fun openFragment(fragment: Fragment) {
        val transaction = this.requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_menu, fragment)
        transaction.commit()
    }

    override fun onItemClickMasUno(holder: ProductoViewHolder) {
        holder.sumarUno()
        holder.itemView.textCantidadNumero.setText(holder.cantidad.toString())
    }

    override fun onItemClickMenosUno(holder: ProductoViewHolder) {
        if (holder.cantidad > 1){
            holder.restarUno()
        }
        holder.itemView.textCantidadNumero.setText(holder.cantidad.toString())
    }

    override fun onItemClickAddProducto(productoSelected: Producto, holder: ProductoViewHolder) {
        createDialogProducto(productoSelected,holder)
    }

    fun createDialogProducto(producto: Producto, holder: ProductoViewHolder){
        var precioTotal : Double = holder.itemView.textCantidadNumero.text.toString().toDouble() * producto.precio.toString().toDouble()

        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_producto, null)

        val mBuilder = AlertDialog.Builder(context)
            .setView(mDialogView)
            .setCancelable(false)

        val mAlertDialog = mBuilder.show()

        mDialogView.textNombreProductoDialog.text = producto.nombre
        mDialogView.textDescripcionProductoDialog.text = producto.descripcion
        mDialogView.textCantidadNumeroDialog.text = holder.itemView.textCantidadNumero.text
        mDialogView.textPrecioNumeroDialog.text = precioTotal.round(2).toString()

        mDialogView.button_cancelar_dialog.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mDialogView.button_agregar_itemcarrito.setOnClickListener {
            agregarBuscarItemCarrito(mDialogView, producto)
            mAlertDialog.dismiss()
        }

    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }

    fun agregarBuscarItemCarrito(mDialogView : View, producto : Producto){
        var flg = 0
        val itemsPorEmailQuery: Query = myRefCarrito.orderByChild("usuario").equalTo(currentUser.email)
        val obtenerProductoCarritoListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var keySearch : String = ""
                var keyActualizar : String = ""
                snapshot.children.forEach { child ->
                    keySearch = child.child("producto").child("key").value.toString()
                    if (keySearch == producto.key){
                        keyActualizar = child.key.toString()
                        flg = 1
                    }
                }
                if (flg == 1){
                    agregarOActualizarItemCarrito(mDialogView,producto,true, keyActualizar)
                } else {
                    agregarOActualizarItemCarrito(mDialogView, producto, false, null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", error.toException())
            }

        }
        itemsPorEmailQuery.addListenerForSingleValueEvent(obtenerProductoCarritoListener)

    }


    fun agregarOActualizarItemCarrito(mDialogView : View, producto : Producto, actualizar : Boolean, keyActualizar : String?){
        if (actualizar){
            val actualizarItemCarritoListener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val cantidadActual = dataSnapshot.child("cantidad").value.toString().toInt()
                    val cantidadNueva = cantidadActual + mDialogView.textCantidadNumeroDialog.text.toString().toInt()
                    val precioActual = dataSnapshot.child("preciototal").value.toString().toDouble()
                    val precioNuevo = precioActual + mDialogView.textPrecioNumeroDialog.text.toString().toDouble()
                    myRefCarrito.child(keyActualizar!!).child("cantidad").setValue(cantidadNueva)
                    myRefCarrito.child(keyActualizar).child("preciototal").setValue(precioNuevo)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                }
            }

            myRefCarrito.child(keyActualizar!!).addListenerForSingleValueEvent(actualizarItemCarritoListener)

        } else {

            val agregarItemCarritoListener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    myRefCarrito.child(dataSnapshot.key.toString()).child("producto").setValue(producto)
                    myRefCarrito.child(dataSnapshot.key.toString()).child("preciototal").setValue(mDialogView.textPrecioNumeroDialog.text.toString().toDouble())
                    myRefCarrito.child(dataSnapshot.key.toString()).child("cantidad").setValue(mDialogView.textCantidadNumeroDialog.text.toString().toInt())
                    myRefCarrito.child(dataSnapshot.key.toString()).child("usuario").setValue(currentUser.email)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                }
            }

            myRefCarrito.child(myRefCarrito.push().key.toString()).addListenerForSingleValueEvent(agregarItemCarritoListener)
        }

    }

}