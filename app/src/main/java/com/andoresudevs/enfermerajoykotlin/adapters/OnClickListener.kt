package com.andoresudevs.enfermerajoykotlin.adapters

import com.andoresudevs.enfermerajoykotlin.models.Doctor
import com.andoresudevs.enfermerajoykotlin.models.Hospital
import com.andoresudevs.enfermerajoykotlin.models.Treatment



interface OnClickListener {

    fun onClickTreatment(treatment: Treatment, position:Int){}

    fun onClickDoctor(doctor:Doctor,position: Int){}

    fun onClickHospital(hospital: Hospital,position: Int){}


}