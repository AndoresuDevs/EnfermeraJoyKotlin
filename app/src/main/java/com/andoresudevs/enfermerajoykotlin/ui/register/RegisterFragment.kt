package com.andoresudevs.enfermerajoykotlin.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.andoresudevs.enfermerajoykotlin.databinding.FragmentRegisterBinding
import com.andoresudevs.enfermerajoykotlin.extensions.observe
import com.andoresudevs.enfermerajoykotlin.states.CrudState
import com.andoresudevs.enfermerajoykotlin.states.DatabaseQueryState

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding=FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(viewModel.state,::registerState)

        binding.btnCancelarRegistro.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnConfirmarRegistro.setOnClickListener {

            val nombre=binding.txtNombreRegistro.text.toString()
            val email=binding.txtCorreoRegistro.text.toString()
            val contra1=binding.txtContraRegistro.text.toString()
            val contra2=binding.txtContra2Registro.text.toString()

            if(nombre==""||email==""||contra1==""||contra2==""){
                Toast.makeText(context,"Debes llenar todos los campos", Toast.LENGTH_SHORT).show()
            }else{
                if(contra1.length<6){
                    Toast.makeText(context,"La contraseña debe tener al menos 6 caracteres",Toast.LENGTH_SHORT).show()
                }else{
                    if(contra1!=contra2){
                        Toast.makeText(context,"Las contraseñas deben ser iguales",Toast.LENGTH_SHORT).show()
                    }else{
                        //REGISTRO DESPUES DE LAS VALIDACIONES
                        viewModel.registrarUsuario(nombre,email,contra1)
                    }
                }
            }
        }
    }

    private fun registerState(state: DatabaseQueryState) {
        when (state) {
            is DatabaseQueryState.waiting -> {

            }
            is DatabaseQueryState.successful->{
                Toast.makeText(context,"Nuevo Usuario Registrado",Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
            }
            is DatabaseQueryState.error->{
                Toast.makeText(context,"Error al registrar",Toast.LENGTH_SHORT).show()
            }
            else -> {

            }
        }
    }



}