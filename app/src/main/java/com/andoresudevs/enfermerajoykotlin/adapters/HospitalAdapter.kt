package com.andoresudevs.enfermerajoykotlin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andoresudevs.enfermerajoykotlin.R
import com.andoresudevs.enfermerajoykotlin.databinding.AdapterHospitalBinding
import com.andoresudevs.enfermerajoykotlin.models.Hospital

class HospitalAdapter (private val hospitales:ArrayList<Hospital>, private val listener: OnClickListener):RecyclerView.Adapter<HospitalAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        var binding = AdapterHospitalBinding.bind(view)
        fun setListener(hospital: Hospital, position: Int){
            binding.root.setOnClickListener { listener.onClickHospital(hospital,position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalAdapter.ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_hospital,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hospital = hospitales[position]
        with(holder){
            setListener(hospital,position)
            binding.txtNombreHospAdap.text=hospital.nombre
            binding.txtEspecialidadHospAdap.text=hospital.especialidad
            binding.txtDireccionHospAdap.text=hospital.direccion

        }
    }

    override fun getItemCount(): Int = hospitales.size


}