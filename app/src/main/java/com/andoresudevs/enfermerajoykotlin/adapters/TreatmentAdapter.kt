package com.andoresudevs.enfermerajoykotlin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andoresudevs.enfermerajoykotlin.R
import com.andoresudevs.enfermerajoykotlin.databinding.AdapterTreatmentBinding
import com.andoresudevs.enfermerajoykotlin.models.Treatment

class TreatmentAdapter(private val tratamientos: ArrayList<Treatment>, private val listener: OnClickListener): RecyclerView.Adapter<TreatmentAdapter.ViewHolder>()
{
    private lateinit var context: Context

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        var binding = AdapterTreatmentBinding.bind(view)
        fun setListener(treatment:Treatment,position: Int){
            binding.root.setOnClickListener { listener.onClickTreatment(treatment,position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_treatment,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tratamiento=tratamientos[position]
        with(holder){
            setListener(tratamiento,position)
            binding.txtFechaAdap.text=tratamiento.fecha
            binding.txtDiagnosticoAdap.text=tratamiento.diagnostico
            binding.txtDoctorAdap.text=tratamiento.medico
            binding.txtClinicaAdap.text=tratamiento.hospital
        }
    }

    override fun getItemCount(): Int =tratamientos.size

}