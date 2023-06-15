package com.andoresudevs.enfermerajoykotlin.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andoresudevs.enfermerajoykotlin.states.DatabaseQueryState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.concurrent.timerTask

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableLiveData<DatabaseQueryState>()
    val state : LiveData<DatabaseQueryState> get() = _state

    fun iniciarSesion(email:String, password:String){
        _state.value = DatabaseQueryState.waiting
        //val auth= FirebaseAuth.getInstance()
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                _state.value = DatabaseQueryState.successful
            }else{
                _state.value = DatabaseQueryState.error
            }
        }
    }
}