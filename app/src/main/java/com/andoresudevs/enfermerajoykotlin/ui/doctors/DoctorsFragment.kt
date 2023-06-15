package com.andoresudevs.enfermerajoykotlin.ui.doctors

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.andoresudevs.enfermerajoykotlin.databinding.FragmentDoctorsBinding
import com.andoresudevs.enfermerajoykotlin.extensions.FormulariesActions
import com.andoresudevs.enfermerajoykotlin.extensions.observe
import com.andoresudevs.enfermerajoykotlin.models.Doctor
import com.andoresudevs.enfermerajoykotlin.states.CrudState
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale
import kotlin.collections.ArrayList


class DoctorsFragment : Fragment(), TextToSpeech.OnInitListener, FormulariesActions {

    private val viewModel:DoctorsViewModel by viewModels()
    private lateinit var binding:FragmentDoctorsBinding
    private val args: DoctorsFragmentArgs by navArgs()

    //CAMPOS
    private lateinit var arrayFields: ArrayList<TextInputEditText>
    private lateinit var arrayFabs: ArrayList<FloatingActionButton>

    //FAB
    private var isFabExtended=false

    //NARRADOR
    private var tts:TextToSpeech?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding=FragmentDoctorsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //NARRADOR
        tts = TextToSpeech(context, this)

        //CAMPOS
        arrayFields = ArrayList()
        loadFieldsAction(binding.formularioDoctores, arrayFields)
        arrayFabs= ArrayList()
        loadFabsAction(binding.root, arrayFabs)

        //MODO
        observe(viewModel.state,::onChangeCrud)
        if (args.modo=="Select") viewModel.select() else { viewModel.create() }

        //SE HABILITAN LOS BOTONES
        setFabClickListeners()

    }

    private fun onChangeCrud(state:CrudState) {
        when (state) {
            is CrudState.Create -> {
                binding.TituloDoctor.text = "Nuevo Doctor"
                binding.fabDoc.visibility = View.GONE
                binding.fabSaveDoc.visibility = View.VISIBLE
                binding.fabCancelDoc.visibility = View.VISIBLE
            }
            is CrudState.Select -> {
                binding.TituloDoctor.text = "Detalles del Doctor"

                if (!viewModel.usarNuevoRegistro){
                    viewModel.setActualDoctor(args.jsonObject)
                  }

                binding.fabSaveDoc.visibility = View.GONE
                binding.fabCancelDoc.visibility = View.GONE
                binding.fabDoc.visibility = View.VISIBLE
                llenarCampos(viewModel.actualDoctor)
                editableFieldsAction(false, arrayFields)
            }
            is CrudState.Update->{
                binding.TituloDoctor.text = "Editando Doctor"
                editableFieldsAction(true,arrayFields)
                closeFabAction(arrayFabs)
                binding.fabDoc.clearAnimation()
                binding.fabDoc.visibility=View.GONE
                binding.fabSaveDoc.visibility = View.VISIBLE
                binding.fabCancelDoc.visibility = View.VISIBLE
            }
            else -> {

            }
        }
    }

    //CAMPOS
    private fun llenarCampos(doc: Doctor){
        binding.nombreDoc.setText(doc.nombre)
        binding.especialidadDoc.setText(doc.especialidad)
        binding.hospitalDoc.setText(doc.hospital)
        binding.telefonoDoc.setText(doc.telefono)
        binding.correoDoc.setText(doc.correo)
    }

    private fun setFabClickListeners(){
        binding.fabDoc.setOnClickListener {
            if (!isFabExtended) openFabAction(arrayFabs) else closeFabAction(arrayFabs)
            isFabExtended=!isFabExtended
        }

        binding.fabCancelDoc.setOnClickListener {
            with(buildCancelBuilderAction(context!!)){
                setPositiveButton("CONFIRMAR") { _, _ -> if(viewModel.state.value==CrudState.Create) findNavController().navigateUp() else { viewModel.select()}; isFabExtended=!isFabExtended }
                show()
            }
        }

        binding.fabSaveDoc.setOnClickListener {
            isFabExtended=!isFabExtended
            val newDoctor= Doctor(
                viewModel.actualDoctor.id,
                binding.nombreDoc.text.toString(),
                binding.especialidadDoc.text.toString(),
                binding.hospitalDoc.text.toString(),
                binding.telefonoDoc.text.toString(),
                binding.correoDoc.text.toString(),
                )

            if(viewModel.state.value==CrudState.Create){
                viewModel.saveDoctor(newDoctor, context!!)
            } else {
                viewModel.updateDoctor(newDoctor, context!!)
            }
        }

        binding.fabEditDoc.setOnClickListener {
            viewModel.edit()
        }

        binding.fabDeleteDoc.setOnClickListener {
            with(buildDeleteBuilderAction(context!!)){
                setPositiveButton("SI") { _, _ -> viewModel.deleteDoctor(context!!); findNavController().navigateUp() }
                show()
            }
        }

        binding.fabShareDoc.setOnClickListener {
            //showShareBuilderAction(viewModel.getObjectInJson(),context!!,layoutInflater, requireActivity() as MainActivity)
            showShareBuilderAction(viewModel.getObjectInJson(),1,context!!,layoutInflater)
        }

        binding.fabSpeakerDoc.setOnClickListener {
            tts?.speak(mensajeToSpeakerAction(arrayFields), TextToSpeech.QUEUE_FLUSH,null,"")
        }

    }

    override fun onInit(status: Int) {
        if (status==TextToSpeech.SUCCESS) tts?.language = Locale("ES")  else println("SPEAKER AVAILABLEN´T")
    }

    override fun onDestroy() {
        if(tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }

        super.onDestroy()
    }


}