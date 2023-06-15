package com.andoresudevs.enfermerajoykotlin.ui.menu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.andoresudevs.enfermerajoykotlin.Login
import com.andoresudevs.enfermerajoykotlin.MainActivity
import com.andoresudevs.enfermerajoykotlin.R
import com.andoresudevs.enfermerajoykotlin.databinding.FragmentMenuBinding


class MenuFragment : Fragment() {
    private val menuViewModel : MenuViewModel by viewModels()
    private lateinit var binding: FragmentMenuBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMenuBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //menuViewModel.nfcWriterMood()
        val activity = requireActivity()
        if (activity is MainActivity) {
            activity.nfcScanViewState(false)
        }

        binding.btnPerfil.setOnClickListener{
            Navigation.findNavController(requireView()).navigate(R.id.action_menu_to_profile)
        }

        binding.btnRecetas.setOnClickListener{
            val action = MenuFragmentDirections.actionMenuToList(0)
            Navigation.findNavController(requireView()).navigate(action)
        }

        binding.btnDoctores.setOnClickListener {
            val action = MenuFragmentDirections.actionMenuToList(1)
            Navigation.findNavController(requireView()).navigate(action)
        }

        binding.btnHospitales.setOnClickListener{
            val action = MenuFragmentDirections.actionMenuToList(2)
            Navigation.findNavController(requireView()).navigate(action)
        }

        binding.btnEscaner.setOnClickListener{
            Navigation.findNavController(requireView()).navigate(R.id.action_menu_to_scanner)
        }

        binding.btnCerrarSesion.setOnClickListener{
            menuViewModel.cerrarSesion()
            Toast.makeText(context,"Hasta pronto", Toast.LENGTH_SHORT).show()
            startActivity(Intent(activity, Login::class.java))
            activity.finish()
        }

        binding.btnEmergencia.setOnClickListener {
            val phoneNo = "8184528939"
            if (!TextUtils.isEmpty(phoneNo)) {
                val dial = "tel:$phoneNo"
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(dial)))
            } else {
                Toast.makeText(context, "Enter a phone number", Toast.LENGTH_SHORT).show()
            }
        }

    }
}