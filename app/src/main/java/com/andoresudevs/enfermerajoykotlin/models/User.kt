package com.andoresudevs.enfermerajoykotlin.models

data class User (
    //CUENTA
    //var id:String="",
    //var contra:String="",
    //var correo:String="",

    //INFO PERSONAL,
    var nombre:String="",
    var fechaNacimiento:String="",
    var genero:String="",
    var estatura:String="",
    var peso:String="",
    var domicilio:String="",
    var telefono:String="",

    //INFO MEDICA
    var nss: String ="",
    var alergias:String="",
    var sangre:String="",
    var donanteDeOrganos:Boolean = false,
    var umf:String="",
    var detalles:String="",

    //CONTACTOS,
    var contacts: ArrayList<Contact> = ArrayList(),
)