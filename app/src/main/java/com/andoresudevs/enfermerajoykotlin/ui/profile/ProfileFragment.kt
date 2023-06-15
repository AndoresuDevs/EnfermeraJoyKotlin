package com.andoresudevs.enfermerajoykotlin.ui.profile

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.andoresudevs.enfermerajoykotlin.MainActivity
import com.andoresudevs.enfermerajoykotlin.databinding.FragmentProfileBinding
import com.andoresudevs.enfermerajoykotlin.extensions.FormulariesActions
import com.andoresudevs.enfermerajoykotlin.extensions.observe
import com.andoresudevs.enfermerajoykotlin.models.User
import com.andoresudevs.enfermerajoykotlin.states.CrudState
import com.andoresudevs.enfermerajoykotlin.states.DatabaseQueryState
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class ProfileFragment : Fragment(), TextToSpeech.OnInitListener, FormulariesActions {

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var binding:FragmentProfileBinding

    //ARREGLOS DE CAMPOS
    private var arrayFields: ArrayList<TextInputEditText> = ArrayList()
    private var arrayFabs: ArrayList<FloatingActionButton> = ArrayList()
    //FAB
    private var isFabExtended=false
    //NARRADOR
    private var tts: TextToSpeech?=null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(viewModel.stateDb,::databaseState)
        observe(viewModel.state,::onChangeCrud)
        viewModel.select()
        viewModel.getUserInfo(arguments)

        println("ARGS: "+arguments?.getString("json",null))


        //NARRADOR
        tts = TextToSpeech(context, this)

        //ACTIONS FROM INTERFACES FORMULARIES ACTIONS
        loadFieldsAction(binding.formularioPerfil, arrayFields)
        loadFabsAction(binding.root, arrayFabs)
        editableFieldsAction(false, arrayFields)

        //SE HABILITAN LOS BOTONES
        setFabClickListeners()

    }


    private fun databaseState(state: DatabaseQueryState) {
        when (state) {
            is DatabaseQueryState.waiting -> {
                binding.formularioPerfil.visibility=View.INVISIBLE
                binding.loadingAnimation.visibility=View.VISIBLE
                binding.loadingAnimation.playAnimation()

            }
            is DatabaseQueryState.successful->{
                binding.formularioPerfil.visibility=View.VISIBLE
                llenarCampos(viewModel.actualUser)
                binding.loadingAnimation.visibility=View.GONE
                binding.loadingAnimation.cancelAnimation()
            }
            is DatabaseQueryState.error->{
                binding.formularioPerfil.visibility=View.VISIBLE
                Toast.makeText(context,"Error al consultar usuario", Toast.LENGTH_SHORT).show()
                binding.loadingAnimation.visibility=View.GONE
                binding.loadingAnimation.cancelAnimation()
            }
            is DatabaseQueryState.sleep->{
                viewModel.sleepDatabase()
            }

        }
    }

    private fun onChangeCrud(state:CrudState) {
        when (state) {
            is CrudState.Select -> {
                binding.TituloPerfil.text = "Mi Perfíl"
                binding.fabSavePerf.visibility = View.GONE
                binding.fabCancelPerf.visibility = View.GONE
                binding.fabPerf.visibility = View.VISIBLE
                editableFieldsAction(false,arrayFields)
            }
            is CrudState.Update->{
                binding.TituloPerfil.text = "Editando Mi Perfíl"
                editableFieldsAction(true,arrayFields)
                closeFabAction(arrayFabs)
                binding.fabPerf.clearAnimation()
                binding.fabPerf.visibility=View.GONE
                binding.fabSavePerf.visibility = View.VISIBLE
                binding.fabCancelPerf.visibility = View.VISIBLE
            }
            else -> {}
        }
    }

    //CAMPOS
    private fun llenarCampos(actualUser: User){
        binding.txtNombreP.setText(actualUser.nombre)
        binding.txtFechaNacimientoP.setText(actualUser.fechaNacimiento)
        binding.txtEstaturaP.setText(actualUser.estatura)
        binding.txtSexoP.setText(actualUser.genero)
        binding.txtPesoP.setText(actualUser.peso)
        binding.txtDomicilioP.setText(actualUser.domicilio)
        binding.txtTelefonoP.setText(actualUser.telefono)
        binding.txtNssP.setText(actualUser.nss)
        binding.txtAlergiasP.setText(actualUser.alergias)
        binding.txtDonanteP.setText(if(actualUser.donanteDeOrganos) "Donante" else "No Donante")
        binding.txtSangreP.setText(actualUser.sangre)
        binding.txtUmfP.setText(actualUser.umf)
        binding.txtDetallesP.setText(actualUser.detalles)
    }

    private fun setFabClickListeners(){
        binding.fabPerf.setOnClickListener {
            if (!isFabExtended) openFabAction(arrayFabs) else closeFabAction(arrayFabs)
            isFabExtended=!isFabExtended
        }

        binding.fabCancelPerf.setOnClickListener {
            with(buildCancelBuilderAction(context!!)){
                setPositiveButton("CONFIRMAR") { _, _ -> if(viewModel.state.value==CrudState.Create) findNavController().navigateUp() else { viewModel.select()}; isFabExtended=!isFabExtended }
                show()
            }
        }

        binding.fabSavePerf.setOnClickListener {
            isFabExtended=!isFabExtended
            val newUser= User(
                binding.txtNombreP.text.toString(),
                binding.txtFechaNacimientoP.text.toString(),
                binding.txtSexoP.text.toString(),
                binding.txtEstaturaP.text.toString(),
                binding.txtPesoP.text.toString(),
                binding.txtDomicilioP.text.toString(),
                binding.txtTelefonoP.text.toString(),
                binding.txtNssP.text.toString(),
                binding.txtAlergiasP.text.toString(),
                binding.txtSangreP.text.toString(),
                donanteDeOrganos = true,
                binding.txtUmfP.text.toString(),
                binding.txtDetallesP.text.toString(),
            )
            viewModel.updateProfile(newUser, context!!)

        }

        binding.fabEditPerf.setOnClickListener {
            viewModel.edit()
        }


        binding.fabSharePerf.setOnClickListener {
            //showShareBuilderAction(viewModel.getObjectInJson(),context!!,layoutInflater, requireActivity() as MainActivity)
            showShareBuilderAction(viewModel.getObjectInJson(),3,context!!,layoutInflater)
        }

        binding.fabSpeakerPerf.setOnClickListener {
            tts?.speak(mensajeToSpeakerAction(arrayFields), TextToSpeech.QUEUE_FLUSH,null,"")
        }

    }

    override fun onInit(status: Int) {
        if (status==TextToSpeech.SUCCESS){
            println("SPEAKER AVAILABLE")
            tts?.language = Locale("ES")
        }else{
            //findViewById<TextView>(R.id.tvStatus).text = "No disponible:((("
            println("SPEAKER AVAILABLEN´T")
        }
    }

    override fun onDestroy() {
        if(tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

}