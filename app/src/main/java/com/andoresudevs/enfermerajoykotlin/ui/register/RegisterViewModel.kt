package com.andoresudevs.enfermerajoykotlin.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andoresudevs.enfermerajoykotlin.models.User
import com.andoresudevs.enfermerajoykotlin.states.DatabaseQueryState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private val _state = MutableLiveData<DatabaseQueryState>()
    val state : LiveData<DatabaseQueryState> get() = _state

    fun registrarUsuario(nombre: String, email: String, pass:String){
        _state.value = DatabaseQueryState.waiting

        database = FirebaseDatabase.getInstance()
        auth = Firebase.auth
        dbReference = FirebaseDatabase.getInstance().reference

        val newUser= User()
        newUser.nombre=nombre
        val userJson = Gson().toJson(newUser)

        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    dbReference.child("Usuarios").child(user.uid).child("userJson").setValue(userJson).addOnCompleteListener{taskInsert ->
                        if (taskInsert.isComplete){
                            _state.value = DatabaseQueryState.successful
                        }else{
                            _state.value = DatabaseQueryState.error
                        }
                    }
                }
            } else {
                _state.value = DatabaseQueryState.error
            }
        }
    }
}