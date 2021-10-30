package com.project.vivian.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.project.vivian.R
import com.project.vivian.cuenta.CuentaFragment
import com.project.vivian.model.Usuario

class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener  {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser : FirebaseUser

    private val database = FirebaseDatabase.getInstance()
    private val myRef : DatabaseReference = database.getReference("usuario")

    companion object{
        fun newInstance() : HomeFragment = HomeFragment()
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

        auth = Firebase.auth

        updateUI()
    }

    override fun onStart() {
        super.onStart()
        currentUser = auth.currentUser!!
    }

    fun updateUI(){
        val usuarioListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val myObject : Usuario? = dataSnapshot.child(currentUser.uid).getValue(Usuario::class.java)
                if (myObject != null) {
                    if (myObject.nombres == ""){
                        val builder = AlertDialog.Builder(context)
                        builder.setMessage(R.string.dialog_actualice_datos)
                            .setCancelable(false)
                            .setPositiveButton("OK") { dialog, id ->
                                val fragment = CuentaFragment.newInstance()
                                openFragment(fragment)
                            }
                        val alert = builder.create()
                        alert.show()
                    }
                }

            }
            override fun onCancelled(error: DatabaseError) {

            }
        }
        myRef.addValueEventListener(usuarioListener)
    }

    fun openFragment(fragment: Fragment) {
        val transaction = this.requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_menu, fragment)
        transaction.commit()
    }
}