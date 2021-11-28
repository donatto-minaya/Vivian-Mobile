package com.project.vivian.home

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
import com.google.firebase.database.*
import com.project.vivian.R
import com.project.vivian.model.Producto
import com.project.vivian.productos.ProductoAdapter
import com.project.vivian.productos.ProductoFragment
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener  {

    private val database = FirebaseDatabase.getInstance()
    private val myRefProducto : DatabaseReference = database.getReference("producto")

    var listProductos = ArrayList<Producto>();

    private lateinit var progressDialog: ProgressDialog

    private val newsList = listOf(
        News("¡Hemos aperturado nueva sede!","https://i.ibb.co/CHhpQBG/nueva-sede.jpg"),
        News("¡Nuevo plato a la carta!","https://i.ibb.co/r5JpJR5/nueva-comida.jpg"),
        News("Implementamos música en vivo","https://i.ibb.co/dr40zny/musica-en-vivo.jpg"),
        News("Tus formas de pago favoritas","https://i.ibb.co/jwCgRhn/nuevas-formas-pago.jpg"),
        News("Nuevas medidas preventidas Covid-19","https://i.ibb.co/d6bMZwz/nuevas-medidas-preventivas.jpg")
    )

    companion object{
        fun newInstance() : HomeFragment = HomeFragment()

        class MyTask(private val fragment : HomeFragment) : AsyncTask<Void, Void, Void>(){
            override fun doInBackground(vararg p0: Void?): Void? {
                return null
            }

            override fun onPreExecute() {
                fragment.progressDialog.show()
            }
        }
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
        inflater.inflate(R.layout.fragment_home,container,false)

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(recyclerProductosTop)
        loadDataNews()
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
                loadDataProductosTop()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        myRefProducto.addValueEventListener(listarProductosListener)
    }

    fun loadDataProductosTop() {
        val adapter = ProductosTopAdapter(listProductos,this)
        recyclerProductosTop?.adapter = adapter
        recyclerProductosTop?.layoutManager = LinearLayoutManager(context)
        progressDialog.dismiss()
    }

    fun loadDataNews() {
        recyclerNews.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
            adapter = NewsAdapter(newsList)
        }
    }

    fun openFragment(fragment: Fragment) {
        val transaction = this.requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_menu, fragment)
        transaction.commit()
    }
}