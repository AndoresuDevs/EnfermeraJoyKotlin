package com.andoresudevs.enfermerajoykotlin.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.andoresudevs.enfermerajoykotlin.adapters.DoctorAdapter
import com.andoresudevs.enfermerajoykotlin.adapters.HospitalAdapter
import com.andoresudevs.enfermerajoykotlin.adapters.OnClickListener
import com.andoresudevs.enfermerajoykotlin.adapters.TreatmentAdapter
import com.andoresudevs.enfermerajoykotlin.databinding.FragmentListBinding
import com.andoresudevs.enfermerajoykotlin.extensions.observe
import com.andoresudevs.enfermerajoykotlin.models.Doctor
import com.andoresudevs.enfermerajoykotlin.models.Hospital
import com.andoresudevs.enfermerajoykotlin.models.Treatment
import com.andoresudevs.enfermerajoykotlin.states.DatabaseQueryState
import com.google.gson.Gson
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.Adapter
import java.util.*
import kotlin.collections.ArrayList

class ListFragment : Fragment(),OnClickListener{

    private lateinit var binding: FragmentListBinding
    private val viewModel: ListViewModel by viewModels()
    private val args:ListFragmentArgs by navArgs()
    private var adapter: Any?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding=FragmentListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.Recycler.layoutManager= LinearLayoutManager(context)

        observe(viewModel.stateDb,::databaseState)

        //REALIZAR LA CONSULTA DEEPNDIENDO EL OBJETO
        when(args.tipoDeObjeto){
            0 ->{ viewModel.getTratamientos() }
            1->{ viewModel.getDoctores() }
            2->{ viewModel.getHospitales() }
        }

        //DIRECCION DEL FAB DEPENDIENDO DEL TIPO DE OBJETO
        binding.fabListAdd.setOnClickListener {
            when(args.tipoDeObjeto){
                0->{
                    //val action=ListFragmentDirections.actionListToTreatment("Create","")
                    val action=ListFragmentDirections.actionListToTreatment()
                    action.modo="Create"
                    action.jsonObject=""
                    Navigation.findNavController(requireView()).navigate(action)
                }
                1-> {
                    val action = ListFragmentDirections.actionListToDoctors()
                    action.modo="Create"
                    action.jsonObject=""
                    Navigation.findNavController(requireView()).navigate(action)
                }
                2-> {
                    val action = ListFragmentDirections.actionListToHospitals()
                    action.modo="Create"
                    action.jsonObject=""
                    Navigation.findNavController(requireView()).navigate(action)
                }
            }
        }



    }

    private fun databaseState(state: DatabaseQueryState) {
        when (state) {
            is DatabaseQueryState.waiting -> {
                binding.loadingListAnimation.visibility=View.VISIBLE
                binding.loadingListAnimation.playAnimation()
            }
            is DatabaseQueryState.successful->{
                llenarLista()
                viewModel.sleepDatabase()
                binding.loadingListAnimation.visibility=View.GONE
                binding.loadingListAnimation.cancelAnimation()
            }
            is DatabaseQueryState.error->{

                Toast.makeText(context,"Error al consultar la información", Toast.LENGTH_SHORT).show()
            }
            is DatabaseQueryState.sleep->{
                binding.loadingListAnimation.visibility=View.GONE
                binding.loadingListAnimation.cancelAnimation()
            }

        }
    }

    private fun llenarLista(){
        when(args.tipoDeObjeto){
            0 ->{
                binding.tituloListas.text="Mis Tratamientos"
                val trats = ArrayList<Treatment>()
                viewModel.getLista().forEach { trat ->
                    trats.add(trat as Treatment)
                }
                adapter=TreatmentAdapter(trats,this)
                binding.Recycler.adapter= adapter as TreatmentAdapter
            }
            1->{

                val docs = ArrayList<Doctor>()
                viewModel.getLista().forEach { doc ->
                    docs.add(doc as Doctor)
                }

                binding.tituloListas.text="Mis Doctores"
                adapter=DoctorAdapter(docs,this)
                binding.Recycler.adapter= adapter as DoctorAdapter

            }
            2->{
                val hosp = ArrayList<Hospital>()
                viewModel.getLista().forEach { hos ->
                    hosp.add(hos as Hospital)
                }
                binding.tituloListas.text="Mis Hospitales"
                adapter=HospitalAdapter(hosp,this)
                binding.Recycler.adapter= adapter as HospitalAdapter

            }
        }
    }

    override fun onClickTreatment(treatment: Treatment, position: Int) {
        super.onClickTreatment(treatment, position)
        val action=ListFragmentDirections.actionListToTreatment()
        action.modo="Select"
        action.jsonObject=Gson().toJson(treatment)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onClickDoctor(doctor: Doctor, position: Int) {
        super.onClickDoctor(doctor, position)
        val action = ListFragmentDirections.actionListToDoctors()
        action.modo="Select"
        action.jsonObject=Gson().toJson(doctor)
        Navigation.findNavController(requireView()).navigate(action)

    }

    override fun onClickHospital(hospital: Hospital, position: Int) {
        super.onClickHospital(hospital, position)
        val action = ListFragmentDirections.actionListToHospitals()
        action.modo="Select"
        action.jsonObject=Gson().toJson(hospital)
        Navigation.findNavController(requireView()).navigate(action)
    }

}