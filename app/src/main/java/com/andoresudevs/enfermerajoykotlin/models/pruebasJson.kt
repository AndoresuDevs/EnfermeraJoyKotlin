package com.andoresudevs.enfermerajoykotlin.models

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class pruebasJson(
    val title: String,
    val author: String,
    val categories: List<String>
) {

    //override fun toString(): String {
    //    return "Category [title: ${this.title}, author: ${this.author}, categories: ${this.categories}]"
    //}

    fun pruebaJSON(objeto:User) {

        val json = """{"title": "Kotlin Tutorial #1", "author": "bezkoder", "categories" : ["Kotlin","Basic"]}"""
        val gson = Gson()

        //el json
        val jsonHosp="""{"nombre": "nombre", "especialidad": "especialidad", "direccion": "direccion", "telefono": "telefono", "correo": "correo"}"""

        //PASA AL OBJETO
        val hosp : Hospital = gson.fromJson(jsonHosp,Hospital::class.java)
        println("> From JSON String:\n$jsonHosp")

        println("NOMBRE: ${hosp.nombre}")
        println("ESP: ${hosp.especialidad}")
        println("DIR: ${hosp.direccion}")
        println("TEL: ${hosp.telefono}")
        println("COR: ${hosp.correo}")

        //VARIOS OBJETOS EN EL json
        val jsonList =
            """[{"title":"Kotlin Tutorial #1","author":"bezkoder","categories":["Kotlin, Basic"]},
			{"title":"Kotlin Tutorial #2","author":"bezkoder","categories":["Kotlin, Practice"]}]"""

        val arrayListTutorialType = object : TypeToken<ArrayList<pruebasJson>>() {}.type
        var tutorials: ArrayList<pruebasJson> = gson.fromJson(jsonList, arrayListTutorialType)

        //DE OBJETO A JSON
        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        //OBJETO
        val tut = pruebasJson("Tut #1", "bezkoder", listOf("cat1", "cat2"))


        //JSON NORMAL

        val jsonFinal: String = gson.toJson(objeto)
        println("LINEAL")
        println(jsonFinal)

        println("")
        //JSON BONITO E INDENTADO
        val jsonFinalBonito: String = gsonPretty.toJson(objeto)
        println("MODO BONITO")

        println(jsonFinalBonito)
    }
}