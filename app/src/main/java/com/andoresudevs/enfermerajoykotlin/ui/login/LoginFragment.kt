package com.andoresudevs.enfermerajoykotlin.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.andoresudevs.enfermerajoykotlin.MainActivity
import com.andoresudevs.enfermerajoykotlin.R
import com.andoresudevs.enfermerajoykotlin.databinding.FragmentLoginBinding
import com.andoresudevs.enfermerajoykotlin.extensions.observe
import com.andoresudevs.enfermerajoykotlin.states.DatabaseQueryState
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private lateinit var binding:FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding=FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe(viewModel.state,::loginState)

        binding.btnIngresar.setOnClickListener{
            iniciarSesion()

        }

        binding.btnRegistrarse.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.action_login_to_register)
        }

        binding.btnRecuperar.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.action_login_to_restore)
        }

    }

    private fun iniciarSesion() {
        val email=binding.txtEmail.text.toString()
        val pass =binding.txtPassword.text.toString()

        if (email.isNotEmpty() && pass.isNotEmpty()){
            //NAVEGAR DE VISTA
            viewModel.iniciarSesion(email,pass)


        }
    }

    private fun loginState(state: DatabaseQueryState) {
        when (state) {
            is DatabaseQueryState.waiting -> {

            }
            is DatabaseQueryState.successful->{
                Toast.makeText(context,"Sesión iniciada", Toast.LENGTH_SHORT).show()
                Navigation.findNavController(requireView()).navigate(R.id.action_login_to_mainActivity)
                activity?.finish()
            }
            is DatabaseQueryState.error->{
                Toast.makeText(context,"Error al inicair sesión", Toast.LENGTH_SHORT).show()
            }
            else -> {

            }
        }
    }
}




