package com.andoresudevs.enfermerajoykotlin.ui.profile

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andoresudevs.enfermerajoykotlin.models.User
import com.andoresudevs.enfermerajoykotlin.states.CrudState
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
class ProfileViewModel @Inject constructor() : ViewModel() {
    //INSTANCIA DEL USUARIO
    var actualUser=User()

    private val _stateDb = MutableLiveData<DatabaseQueryState>()
    val stateDb : LiveData<DatabaseQueryState> get() = _stateDb

    private val _state = MutableLiveData<CrudState>()
    val state : LiveData<CrudState> get() = _state


    fun edit(){
        _state.value = CrudState.Update
    }

    fun select() {
        _state.value = CrudState.Select
    }

    fun sleepDatabase(){
        _stateDb.value=DatabaseQueryState.sleep
    }

    fun updateProfile(newUser:User, context: Context){
        val userJson = Gson().toJson(newUser)
        val id =FirebaseAuth.getInstance().currentUser?.uid.toString()
        val dbReference = FirebaseDatabase.getInstance().reference
        dbReference.child("Usuarios").child(id).child("userJson").setValue(userJson).addOnCompleteListener{taskInsert ->
            if (taskInsert.isComplete){
                actualUser=newUser
                _state.value = CrudState.Select
                Toast.makeText(context,"ACTUALIZADO", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"HUBO UN ERROR AL ACTUALIZAR", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getObjectInJson(): String =Gson().toJson(actualUser)

    fun getUserInfo(arguments: Bundle?) {
        _stateDb.value = DatabaseQueryState.waiting

        if(arguments != null){
            val json = arguments.getString("json",null)
            actualUser=Gson().fromJson(json,User::class.java)
            _stateDb.value = DatabaseQueryState.successful
        }else{
            val id =FirebaseAuth.getInstance().currentUser?.uid.toString()
            FirebaseDatabase.getInstance().reference.child("Usuarios").child(id).child("userJson").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        actualUser=Gson().fromJson(snapshot.value.toString(), User::class.java)
                        _stateDb.value = DatabaseQueryState.successful
                    }else{
                        _stateDb.value = DatabaseQueryState.error
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    _stateDb.value = DatabaseQueryState.error
                }
            })
        }

    }


}