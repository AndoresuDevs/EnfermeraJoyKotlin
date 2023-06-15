package com.andoresudevs.enfermerajoykotlin.ui.hospitals

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andoresudevs.enfermerajoykotlin.models.Hospital
import com.andoresudevs.enfermerajoykotlin.states.CrudState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HospitalsViewModel @Inject constructor() : ViewModel() {
    var actualHospital=Hospital()
    var usarNuevoRegistro=false

    private val _state = MutableLiveData<CrudState>()
    val state : LiveData<CrudState> get() = _state



    fun create(){
        _state.value = CrudState.Create
    }

    fun edit(){
        _state.value = CrudState.Update
    }

    fun select() {
        _state.value = CrudState.Select
    }

    fun setActualHospital(jsonObject:String){
        actualHospital=Gson().fromJson(jsonObject,Hospital::class.java)
    }

    fun saveHospital(Hosp:Hospital, context: Context){
        val id = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val dbReference = FirebaseDatabase.getInstance().reference
        val newId=dbReference.push().key.toString()

        Hosp.id=newId
        val json = Gson().toJson(Hosp)
        dbReference.child("Usuarios").child(id).child("hospitales").child(newId).setValue(json).addOnCompleteListener{taskInsert ->
            if (taskInsert.isComplete){
                actualHospital=Hosp
                usarNuevoRegistro=true
                _state.value = CrudState.Select
                Toast.makeText(context,"GUARDADO", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"HUBO UN ERROR AL GUARDAR", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateHospital(Hosp:Hospital, context: Context){
        val id = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val dbReference = FirebaseDatabase.getInstance().reference
        val newId=Hosp.id
        val json = Gson().toJson(Hosp)

        dbReference.child("Usuarios").child(id).child("hospitales").child(newId).setValue(json).addOnCompleteListener{taskInsert ->
            if (taskInsert.isComplete){
                actualHospital=Hosp
                usarNuevoRegistro=true
                _state.value = CrudState.Select
                Toast.makeText(context,"ACTUALIZADO", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"HUBO UN ERROR AL ACTUALIZAR", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteHospital(context:Context){
        val idUser =FirebaseAuth.getInstance().currentUser?.uid.toString()
        val dbReference = FirebaseDatabase.getInstance().reference
        val id = actualHospital.id

        dbReference.child("Usuarios").child(idUser).child("hospitales").child(id).removeValue().addOnCompleteListener { delete->
            if(delete.isComplete){
                Toast.makeText(context,"BORRADO", Toast.LENGTH_SHORT).show()
            }
            if (delete.isCanceled){
                Toast.makeText(context,"HUBO EN ERROR AL BORRAR", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getObjectInJson(): String =Gson().toJson(actualHospital)
}