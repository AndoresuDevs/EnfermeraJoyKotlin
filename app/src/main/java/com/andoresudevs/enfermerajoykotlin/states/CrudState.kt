package com.andoresudevs.enfermerajoykotlin.states

sealed class CrudState {
    object Create:CrudState()
    object Select:CrudState()
    object Update:CrudState()
    object Delete:CrudState()

}