package com.andoresudevs.enfermerajoykotlin.ui.list

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andoresudevs.enfermerajoykotlin.models.Doctor
import com.andoresudevs.enfermerajoykotlin.models.Hospital
import com.andoresudevs.enfermerajoykotlin.models.Treatment
import com.andoresudevs.enfermerajoykotlin.states.DatabaseQueryState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor() : ViewModel() {
    private val _stateDb = MutableLiveData<DatabaseQueryState>()
    val stateDb : LiveData<DatabaseQueryState> get() = _stateDb

    private var doctores= ArrayList<Doctor>()
    private var tratamientos= ArrayList<Treatment>()
    private var hospitales=ArrayList<Hospital>()
    private val lista= ArrayList<Any>()

    fun sleepDatabase(){
        _stateDb.value=DatabaseQueryState.sleep
    }

    fun getLista():ArrayList<Any>{
        return lista
    }

    fun getTratamientos(){
        _stateDb.value = DatabaseQueryState.waiting

        val dbReference = FirebaseDatabase.getInstance().reference
        val id = FirebaseAuth.getInstance().currentUser?.uid.toString()

        dbReference.child("Usuarios").child(id).child("tratamientos").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    lista.clear()
                    snapshot.children.forEach {node->
                        lista.add(Gson().fromJson(node.value.toString(),Treatment::class.java))
                    }
                    _stateDb.value = DatabaseQueryState.successful
                } else {
                    _stateDb.value = DatabaseQueryState.error
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _stateDb.value = DatabaseQueryState.error
            }

        })
    }



    fun getDoctores(){
        _stateDb.value = DatabaseQueryState.waiting

        val dbReference = FirebaseDatabase.getInstance().reference
        val id = FirebaseAuth.getInstance().currentUser?.uid.toString()

        dbReference.child("Usuarios").child(id).child("doctores").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    lista.clear()
                    snapshot.children.forEach {node->
                        lista.add(Gson().fromJson(node.value.toString(),Doctor::class.java))
                    }
                    _stateDb.value = DatabaseQueryState.successful
                } else {
                    _stateDb.value = DatabaseQueryState.error
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _stateDb.value = DatabaseQueryState.error
            }

        })
    }

    fun getHospitales(){
        _stateDb.value = DatabaseQueryState.waiting

        val dbReference = FirebaseDatabase.getInstance().reference
        val id = FirebaseAuth.getInstance().currentUser?.uid.toString()

        dbReference.child("Usuarios").child(id).child("hospitales").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    lista.clear()
                    snapshot.children.forEach {node->
                        lista.add(Gson().fromJson(node.value.toString(),Hospital::class.java))
                    }
                    _stateDb.value = DatabaseQueryState.successful
                } else {
                    _stateDb.value = DatabaseQueryState.error
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _stateDb.value = DatabaseQueryState.error
            }

        })
    }


}