package com.andoresudevs.enfermerajoykotlin.ui.restorePassword

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.andoresudevs.enfermerajoykotlin.R
import com.andoresudevs.enfermerajoykotlin.databinding.FragmentRestorePasswordBinding

class RestorePasswordFragment : Fragment() {

    private lateinit var binding: FragmentRestorePasswordBinding
    private val viewModel: RestorePasswordViewModel by viewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding=FragmentRestorePasswordBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancelarRecuperar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnConfirmarRecuperar.setOnClickListener {

        }
    }


}