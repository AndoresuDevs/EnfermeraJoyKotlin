package com.andoresudevs.enfermerajoykotlin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andoresudevs.enfermerajoykotlin.R
import com.andoresudevs.enfermerajoykotlin.databinding.AdapterDoctorBinding
import com.andoresudevs.enfermerajoykotlin.models.Doctor

class DoctorAdapter (private val doctores:ArrayList<Doctor>, private val listener: OnClickListener): RecyclerView.Adapter<DoctorAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        var binding = AdapterDoctorBinding.bind(view)
        fun setListener(doctor: Doctor,position: Int){
            binding.root.setOnClickListener { listener.onClickDoctor(doctor,position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context=parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_doctor,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doctor=doctores[position]
        with(holder){
            setListener(doctor,position)
            binding.txtEspecialidadAdap.text=doctor.especialidad
            binding.txtNombreAdap.text=doctor.nombre
            binding.txtTelefonoAdap.text=doctor.telefono
        }
    }

    override fun getItemCount(): Int = doctores.size

}