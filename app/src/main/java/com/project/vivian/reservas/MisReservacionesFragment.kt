package com.project.vivian.reservas

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.project.vivian.R
import com.project.vivian.home.HomeFragment
import com.project.vivian.model.Reserva
import kotlinx.android.synthetic.main.fragment_mis_reservaciones.*
import android.content.DialogInterface




class MisReservacionesFragment : Fragment(), AdapterView.OnItemSelectedListener, MisReservacionesAdapter.ItemClickListener {

    private val database = FirebaseDatabase.getInstance()
    private val myRef : DatabaseReference = database.getReference("reserva")
    var listReservas = ArrayList<Reserva>();

    private lateinit var progressDialog: ProgressDialog

    companion object{
        fun newInstance() : MisReservacionesFragment = MisReservacionesFragment()

        class MyTask(private val fragment : MisReservacionesFragment) : AsyncTask<Void, Void, Void>(){
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
        inflater.inflate(R.layout.fragment_mis_reservaciones, container,false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //reservaRepository.listarReservasFirebase()
        listReservas.clear()
        setupRecyclerView(recyclerReservas)

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun setupRecyclerView(recyclerView: RecyclerView){

        val listarReservaListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listReservas.clear()
                dataSnapshot.children.forEach { child ->
                    val reserva: Reserva? =
                        Reserva(
                            child.child("nombreCliente").value.toString(),
                            child.child("dni").value.toString(),
                            child.child("fecha").value.toString(),
                            child.child("mesa").value.toString().toInt(),
                            child.child("turno").value.toString(),
                            child.key
                        )
                    reserva?.let { listReservas.add(it) }
                }
                loadData()
                progressDialog.dismiss()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        myRef.addValueEventListener(listarReservaListener)
    }

    override fun onItemClickNoteUpdate(reservaSelected: Reserva) {
        val fragment = ReservasFragment.newInstance()
        val args = Bundle()
        args.putString("nombresSend",reservaSelected.nombreCliente)
        args.putString("apellidosSend",reservaSelected.dni)
        args.putString("fechaSend",reservaSelected.fecha)
        args.putString("mesaSend",reservaSelected.mesa.toString())
        args.putString("turnoSend",reservaSelected.turno)
        args.putString("idSend",reservaSelected.key)
        fragment.arguments = args
        openFragment(fragment)
    }

    override fun onItemClickNoteDelete(reservaSelected: Reserva) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(R.string.text_delete_confirm)
            .setCancelable(false)
            .setPositiveButton("Si") { dialog, id ->
                myRef.child(reservaSelected.key.toString()).removeValue()
                Toast.makeText(context,"Reserva eliminada correctamente",Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    fun loadData(){
        val adapter = MisReservacionesAdapter(listReservas,this)
        recyclerReservas?.adapter = adapter
        recyclerReservas?.layoutManager = GridLayoutManager(context,2)
    }

    fun openFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_menu, fragment)
        transaction.commit()
    }


}