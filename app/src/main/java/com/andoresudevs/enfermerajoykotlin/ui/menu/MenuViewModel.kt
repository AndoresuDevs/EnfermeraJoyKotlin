package com.andoresudevs.enfermerajoykotlin.ui.menu

import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andoresudevs.enfermerajoykotlin.states.CrudState
import com.andoresudevs.enfermerajoykotlin.states.NfcState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableLiveData<NfcState>()
    val state : LiveData<NfcState> get() = _state


    fun cerrarSesion(){
        FirebaseAuth.getInstance().signOut()
    }

}