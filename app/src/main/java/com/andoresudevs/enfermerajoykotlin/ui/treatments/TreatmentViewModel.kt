package com.andoresudevs.enfermerajoykotlin.ui.treatments

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andoresudevs.enfermerajoykotlin.extensions.CrudTemplate
import com.andoresudevs.enfermerajoykotlin.models.Treatment
import com.andoresudevs.enfermerajoykotlin.states.CrudState
import com.andoresudevs.enfermerajoykotlin.states.DatabaseQueryState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TreatmentViewModel @Inject constructor(): ViewModel(), CrudTemplate {
    //INSTANCIA DEL TRATAMIENTO
    var actualTreatment= Treatment()
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

    fun setActualTreatment(jsonObject: String){
        actualTreatment = Gson().fromJson(jsonObject, Treatment::class.java)
    }

    fun saveTreatement(Treat:Treatment, context:Context) {
        val id = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val dbReference = FirebaseDatabase.getInstance().reference
        val newId=dbReference.push().key.toString()

        Treat.id=newId
        val json = Gson().toJson(Treat)
        dbReference.child("Usuarios").child(id).child("tratamientos").child(newId).setValue(json).addOnCompleteListener{taskInsert ->
            if (taskInsert.isComplete){
                actualTreatment=Treat
                usarNuevoRegistro=true
                _state.value = CrudState.Select
                Toast.makeText(context,"GUARDADO", Toast.LENGTH_SHORT).show()
             }else{
                Toast.makeText(context,"HUBO UN ERROR AL GUARDAR", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateTreatement(Treat:Treatment, context:Context) {
        val id = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val dbReference = FirebaseDatabase.getInstance().reference
        val newId=Treat.id
        val json = Gson().toJson(Treat)

        dbReference.child("Usuarios").child(id).child("tratamientos").child(newId).setValue(json).addOnCompleteListener{taskInsert ->
            if (taskInsert.isComplete){
                actualTreatment=Treat
                usarNuevoRegistro=true
                _state.value = CrudState.Select
                Toast.makeText(context,"ACTUALIZADO", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"HUBO UN ERROR AL ACTUALIZAR", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteTreatment(context:Context){
        val idUser =FirebaseAuth.getInstance().currentUser?.uid.toString()
        val dbReference = FirebaseDatabase.getInstance().reference
        val id = actualTreatment.id

        dbReference.child("Usuarios").child(idUser).child("tratamientos").child(id).removeValue().addOnCompleteListener { delete->
            if(delete.isComplete){
                Toast.makeText(context,"BORRADO", Toast.LENGTH_SHORT).show()
            }
            if (delete.isCanceled){
                Toast.makeText(context,"HUBO EN ERROR AL BORRAR", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getObjectInJson(): String =Gson().toJson(actualTreatment)

}