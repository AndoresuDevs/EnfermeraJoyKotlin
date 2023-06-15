package com.andoresudevs.enfermerajoykotlin.ui.treatments

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.andoresudevs.enfermerajoykotlin.databinding.FragmentTreatmentBinding
import com.andoresudevs.enfermerajoykotlin.extensions.FormulariesActions
import com.andoresudevs.enfermerajoykotlin.extensions.observe
import com.andoresudevs.enfermerajoykotlin.models.Treatment
import com.andoresudevs.enfermerajoykotlin.states.CrudState
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import java.util.*


class TreatmentFragment : Fragment(), TextToSpeech.OnInitListener, FormulariesActions{
    private val viewModel : TreatmentViewModel by viewModels()
    private lateinit var binding: FragmentTreatmentBinding
    private val args: TreatmentFragmentArgs by navArgs()

    //CAMPOS
    private lateinit var arrayFields: ArrayList<TextInputEditText>
    private lateinit var arrayFabs: ArrayList<FloatingActionButton>

    //FAB
    private var isFabExtended=false

    //NARRADOR
    private var tts:TextToSpeech?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding=FragmentTreatmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //NARRADOR
        tts = TextToSpeech(context, this)

        //CAMPOS
        arrayFields = ArrayList()
        loadFieldsAction(binding.formularioTratamiento, arrayFields)
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
                binding.TituloTratamiento.text = "Nuevo Tratamiento"
                binding.fabTrat.visibility = View.GONE
                binding.fabSaveTrat.visibility = View.VISIBLE
                binding.fabCancelTrat.visibility = View.VISIBLE
            }
            is CrudState.Select -> {
                binding.TituloTratamiento.text = "Detalles del Tratamiento"

                if (!viewModel.usarNuevoRegistro){
                    viewModel.setActualTreatment(args.jsonObject)
                 }

                binding.fabSaveTrat.visibility = View.GONE
                binding.fabCancelTrat.visibility = View.GONE
                binding.fabTrat.visibility = View.VISIBLE
                llenarCampos(viewModel.actualTreatment)
                editableFieldsAction(false,arrayFields)
            }
            is CrudState.Update->{
                binding.TituloTratamiento.text = "Editando Tratamiento"
                editableFieldsAction(true,arrayFields)
                closeFabAction(arrayFabs)
                binding.fabTrat.clearAnimation()
                binding.fabTrat.visibility=View.GONE
                binding.fabSaveTrat.visibility = View.VISIBLE
                binding.fabCancelTrat.visibility = View.VISIBLE
            }
            else -> {

            }
        }
    }

    private fun llenarCampos(treat: Treatment){
        binding.txtFechaTrat.setText(treat.fecha)
        binding.txtNombreTrat.setText(treat.nombre)
        binding.txtPesoTrat.setText(treat.peso)
        binding.txtEstaturaTrat.setText(treat.estatura)
        binding.txtEdadTrat.setText(treat.edad)
        binding.txtSexoTrat.setText(treat.sexo)
        binding.txtMedicoTrat.setText(treat.medico)
        binding.txtHospitalTrat.setText(treat.hospital)
        binding.txtDiagnosticoTrat.setText(treat.diagnostico)
        binding.txtDetallesTrat.setText(treat.detalles)
    }

    private fun setFabClickListeners(){
        binding.fabTrat.setOnClickListener {
            if (!isFabExtended) openFabAction(arrayFabs) else closeFabAction(arrayFabs)
            isFabExtended=!isFabExtended
        }

        binding.fabCancelTrat.setOnClickListener {
            with(buildCancelBuilderAction(context!!)){
                setPositiveButton("CONFIRMAR") { _, _ -> if(viewModel.state.value==CrudState.Create) findNavController().navigateUp() else { viewModel.select()}; isFabExtended=!isFabExtended }
                show()
            }
        }

        binding.fabSaveTrat.setOnClickListener {
            isFabExtended=!isFabExtended
            val newTreatment=Treatment(
                viewModel.actualTreatment.id,
                binding.txtFechaTrat.text.toString(),
                binding.txtNombreTrat.text.toString(),
                binding.txtPesoTrat.text.toString(),
                binding.txtEstaturaTrat.text.toString(),
                binding.txtEdadTrat.text.toString(),
                binding.txtSexoTrat.text.toString(),
                binding.txtMedicoTrat.text.toString(),
                binding.txtHospitalTrat.text.toString(),
                binding.txtDiagnosticoTrat.text.toString(),
                binding.txtDetallesTrat.text.toString(),
            )

            if(viewModel.state.value==CrudState.Create){
                viewModel.saveTreatement(newTreatment, context!!)
            } else {
                viewModel.updateTreatement(newTreatment, context!!)
            }
        }

        binding.fabEditTrat.setOnClickListener {
            viewModel.edit()
        }

        binding.fabDeleteTrat.setOnClickListener {
            with(buildDeleteBuilderAction(context!!)){
                setPositiveButton("SI") { _, _ -> viewModel.deleteTreatment(context!!); findNavController().navigateUp() }
                show()
            }
        }

        binding.fabShareTrat.setOnClickListener {
            //showShareBuilderAction(viewModel.getObjectInJson(),context!!,layoutInflater, requireActivity() as MainActivity)
            showShareBuilderAction(viewModel.getObjectInJson(),0,context!!,layoutInflater)
        }

        binding.fabSpeakerTrat.setOnClickListener {
            if (tts?.isSpeaking == true) tts?.stop() else tts?.speak(mensajeToSpeakerAction(arrayFields), TextToSpeech.QUEUE_FLUSH,null,"")
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