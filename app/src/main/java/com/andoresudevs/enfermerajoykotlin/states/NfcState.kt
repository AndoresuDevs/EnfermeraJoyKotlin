package com.andoresudevs.enfermerajoykotlin.states

sealed class NfcState {
    object Available: NfcState()
    object NotAvailable: NfcState()
    object NfcReader:NfcState()
    object NfcWriter:NfcState()

}