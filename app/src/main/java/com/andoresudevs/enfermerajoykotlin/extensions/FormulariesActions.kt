package com.andoresudevs.enfermerajoykotlin.extensions

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.nfc.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.get
import com.andoresudevs.enfermerajoykotlin.MainActivity
import com.andoresudevs.enfermerajoykotlin.NfcWriterActivity
import com.andoresudevs.enfermerajoykotlin.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder


interface FormulariesActions{
    //BUILDER COMPARTIR
    fun showShareBuilderAction(jsonObject:String,tipo:Int,context:Context, layoutInflater:LayoutInflater) {
        val builder = AlertDialog.Builder(context)
        val vis: View = layoutInflater.inflate(R.layout.alertdialog_share, null)
        builder.setView(vis)
        val btnCancelar = vis.findViewById<ImageView>(R.id.btnCancelShare)
        val btnQR = vis.findViewById<MaterialButton>(R.id.btnShareQR)
        val btnNFC = vis.findViewById<MaterialButton>(R.id.btnShareNFC)
        val alertDialog = builder.create()

        btnCancelar.setOnClickListener { alertDialog.cancel() }
        btnQR.setOnClickListener { showBuilderWithQR(jsonObject,context, layoutInflater) }
        btnNFC.setOnClickListener {
            val intent = Intent(context, NfcWriterActivity::class.java)
            intent.putExtra("json", jsonObject)
            intent.putExtra("tipo", tipo)
            startActivity(context,intent,null)
            alertDialog.cancel()
        }
        alertDialog.show()
    }

    //BUILDER QR
    private fun showBuilderWithQR(jsonObject:String, context:Context, layoutInflater:LayoutInflater) {
        val builder = AlertDialog.Builder(context)
        val vis: View = layoutInflater.inflate(R.layout.alertdialog_qr, null)
        builder.setView(vis)
        val btnCancelar = vis.findViewById<ImageView>(R.id.btnCancelQr)
        val qr = vis.findViewById<ImageView>(R.id.QR)
        qr.setImageBitmap(generateQR(jsonObject))

        val alertDialog = builder.create()

        btnCancelar.setOnClickListener { alertDialog.cancel() }

        alertDialog.show()
    }

    //GENERADOR DE QR
    private fun generateQR(json:String): Bitmap {
        return BarcodeEncoder().encodeBitmap(
            json,
            BarcodeFormat.QR_CODE,
            750,
            750
        )
    }

    //ANIMACIONES PARA LOS FLOATING ACTION BUTTON'S
    fun closeFabAction(arrayFabs: ArrayList<FloatingActionButton> ){
        //ESTA FUNCION SE BASA EN LOS TAGS DE CADA ELEMENTO DEL XML DE LA VISTA
        arrayFabs.forEach {
            with(it){
                when(tag.toString()){
                    "fabCrud"->{
                        visibility= View.GONE
                        startAnimation(AnimationUtils.loadAnimation(context, R.anim.to_bottom_animation))
                        isClickable=false
                    }
                    "fab"->{
                        startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_close_animation))
                    }
                    "fabCreate"->{

                    }
                }
            }
        }

    }

    fun openFabAction(arrayFabs: ArrayList<FloatingActionButton>){
        arrayFabs.forEach {
            with(it){
                when(tag.toString()){
                    "fabCrud"->{
                        visibility= View.VISIBLE
                        startAnimation((AnimationUtils.loadAnimation(context, R.anim.from_bottom_animation)))
                        isClickable=true
                    }
                    "fab"->{
                        startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_open_animation))
                    }
                    "fabCreate"->{

                    }else->{

                    }
                }
            }
        }
    }

    //ALERTS DIALOGS
    //CREA UN TEMPLATE DEL ALERT SIN EL POSITIVE BUTTON PARA PERSONALIZARLO
    //EN LA CLASE QUE SE VA A USAR
    fun buildDeleteBuilderAction(context: Context):AlertDialog.Builder{
         return with(AlertDialog.Builder(context)){
            setCancelable(false)
            setTitle("¿Desea Borrar?")
            setMessage("Este cambio no se puede recuperar")
            setNegativeButton("NO") { _, _ -> }
        }
    }

    fun buildCancelBuilderAction(context: Context):AlertDialog.Builder{
        return with(AlertDialog.Builder(context)){
            setCancelable(false)
            setTitle("¿Desea Cancelar?")
            setMessage("Si cancelas no se guardaran los cambios")
            setNegativeButton("MANTENER") { _, _ -> }
        }
    }

    //CARGAR ELEMENTOS DE LA VISTA
    fun loadFieldsAction(contenedor: ViewGroup, arrayFields: ArrayList<TextInputEditText>){
        for (i in 0 until contenedor.childCount) {
            if (contenedor[i] is TextInputEditText) arrayFields.add(contenedor[i] as TextInputEditText)
            if (contenedor[i] is ViewGroup) loadFieldsAction(contenedor[i] as ViewGroup,arrayFields)
        }
    }

    fun loadFabsAction(contenedor: ViewGroup, arrayFabs: ArrayList<FloatingActionButton>){
        for (i in 0 until contenedor.childCount) {
            if (contenedor[i] is FloatingActionButton) arrayFabs.add(contenedor[i] as FloatingActionButton)
        }
    }

    //CAMPOS EDITABLES
    fun editableFieldsAction(editable:Boolean, arrayFields: ArrayList<TextInputEditText>){
        arrayFields.forEach { it.isEnabled=editable}
    }

    //ENCAPSULA EL MENSAJE PARA QUE SEA LEIDO POR EL SPEAKER
    fun mensajeToSpeakerAction(arrayFields: ArrayList<TextInputEditText>):String{
        var mensaje =""
        arrayFields.forEach {
            mensaje = "$mensaje, ${it.hint}, ${it.text},"
        }
        return mensaje
    }

    fun startLoadingAnimationAction(){

    }

    fun stopLoadingAnimationAction(){

    }

}