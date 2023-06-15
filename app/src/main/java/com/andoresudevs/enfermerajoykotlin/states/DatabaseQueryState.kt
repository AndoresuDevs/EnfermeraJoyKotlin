package com.andoresudevs.enfermerajoykotlin.states

sealed class DatabaseQueryState {
    object waiting: DatabaseQueryState()
    object successful: DatabaseQueryState()
    object error: DatabaseQueryState()
    object sleep: DatabaseQueryState()
}