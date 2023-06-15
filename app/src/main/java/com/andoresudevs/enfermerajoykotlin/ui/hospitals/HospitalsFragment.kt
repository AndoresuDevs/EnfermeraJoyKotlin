package com.andoresudevs.enfermerajoykotlin.ui.hospitals

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.andoresudevs.enfermerajoykotlin.MainActivity
import com.andoresudevs.enfermerajoykotlin.databinding.FragmentHospitalsBinding
import com.andoresudevs.enfermerajoykotlin.extensions.FormulariesActions
import com.andoresudevs.enfermerajoykotlin.extensions.observe
import com.andoresudevs.enfermerajoykotlin.models.Hospital
import com.andoresudevs.enfermerajoykotlin.states.CrudState
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale

class HospitalsFragment : Fragment(), TextToSpeech.OnInitListener, FormulariesActions {
    private val viewModel: HospitalsViewModel by viewModels()
    private lateinit var binding: FragmentHospitalsBinding
    private val args: HospitalsFragmentArgs by navArgs()

    //CAMPOS
    private lateinit var arrayFields: ArrayList<TextInputEditText>
    private lateinit var arrayFabs: ArrayList<FloatingActionButton>

    //FAB
    private var isFabExtended=false

    //NARRADOR
    private var tts: TextToSpeech?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding=FragmentHospitalsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //NARRADOR
        tts = TextToSpeech(context, this)

        //CAMPOS
        arrayFields = ArrayList()
        loadFieldsAction(binding.formularioHospitales, arrayFields)
        arrayFabs= ArrayList()
        loadFabsAction(binding.root, arrayFabs)

        //MODO
        observe(viewModel.state,::onChangeCrud)
        if (args.modo=="Select") viewModel.select() else { viewModel.create() }

        //FLOATING ACTION BUTTONS LISTENERS
        setFabClickListeners()
    }

    private fun onChangeCrud(state:CrudState) {
        when (state) {
            is CrudState.Create -> {
                binding.TituloHospital.text = "Nuevo Hospital"
                binding.fabHosp.visibility = View.GONE
                binding.fabSaveHosp.visibility = View.VISIBLE
                binding.fabCancelHosp.visibility = View.VISIBLE
            }
            is CrudState.Select -> {
                binding.TituloHospital.text = "Detalles del Hospital"

                if (!viewModel.usarNuevoRegistro){
                    viewModel.setActualHospital(args.jsonObject)
                    println("JSON OBJECT FROM ARGS: ${args.jsonObject}")
                }
                binding.fabSaveHosp.visibility = View.GONE
                binding.fabCancelHosp.visibility = View.GONE
                binding.fabHosp.visibility = View.VISIBLE
                llenarCampos(viewModel.actualHospital)
                editableFieldsAction(false, arrayFields)
            }
            is CrudState.Update->{
                binding.TituloHospital.text = "Editando Hospital"
                editableFieldsAction(true,arrayFields)
                closeFabAction(arrayFabs)
                binding.fabHosp.clearAnimation()
                binding.fabHosp.visibility=View.GONE
                binding.fabSaveHosp.visibility = View.VISIBLE
                binding.fabCancelHosp.visibility = View.VISIBLE
            }
            else -> {

            }
        }
    }

    //CAMPOS
    private fun llenarCampos(hosp: Hospital){
        binding.nombreHosp.setText(hosp.nombre)
        binding.especialidadHosp.setText(hosp.especialidad)
        binding.direccionHosp.setText(hosp.direccion)
        binding.telefonoHosp.setText(hosp.telefono)
        binding.correoHosp.setText(hosp.correo)
    }


    private fun setFabClickListeners(){
        binding.fabHosp.setOnClickListener {
            if (!isFabExtended) openFabAction(arrayFabs) else closeFabAction(arrayFabs)
            isFabExtended=!isFabExtended
        }

        binding.fabCancelHosp.setOnClickListener {
            with(buildCancelBuilderAction(context!!)){
                setPositiveButton("CONFIRMAR") { _, _ -> if(viewModel.state.value==CrudState.Create) findNavController().navigateUp() else { viewModel.select()}; isFabExtended=!isFabExtended }
                show()
            }
        }

        binding.fabSaveHosp.setOnClickListener {
            isFabExtended=!isFabExtended
            val newHospital= Hospital(
                viewModel.actualHospital.id,
                binding.nombreHosp.text.toString(),
                binding.especialidadHosp.text.toString(),
                binding.direccionHosp.text.toString(),
                binding.telefonoHosp.text.toString(),
                binding.correoHosp.text.toString(),
            )

            if(viewModel.state.value==CrudState.Create){
                //SE BORRARAN LOS DOS CONTEXT :)
                viewModel.saveHospital(newHospital, context!!)
            } else {
                viewModel.updateHospital(newHospital, context!!)
            }
        }

        binding.fabEditHosp.setOnClickListener {
            viewModel.edit()
        }

        binding.fabDeleteHosp.setOnClickListener {
            with(buildDeleteBuilderAction(context!!)){
                setPositiveButton("SI") { _, _ -> viewModel.deleteHospital(context!!); findNavController().navigateUp() }
                show()
            }
        }

        binding.fabShareHosp.setOnClickListener {
            //showShareBuilderAction(viewModel.getObjectInJson(),context!!,layoutInflater, requireActivity() as MainActivity)
            showShareBuilderAction(viewModel.getObjectInJson(),2,context!!,layoutInflater)
        }

        binding.fabSpeakerHosp.setOnClickListener {
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