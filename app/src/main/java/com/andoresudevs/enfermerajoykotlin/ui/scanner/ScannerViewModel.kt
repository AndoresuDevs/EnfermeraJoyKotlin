package com.andoresudevs.enfermerajoykotlin.ui.scanner

import android.content.Context
import android.nfc.NfcAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andoresudevs.enfermerajoykotlin.models.User
import com.andoresudevs.enfermerajoykotlin.states.NfcState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(): ViewModel() {

    private val _state = MutableLiveData<NfcState>()
    val state : LiveData<NfcState>
        get() = _state

    fun nfcReaderMood(){
        _state.value = NfcState.NfcReader
    }


}