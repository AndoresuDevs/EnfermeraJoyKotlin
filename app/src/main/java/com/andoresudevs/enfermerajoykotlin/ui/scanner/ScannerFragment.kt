package com.andoresudevs.enfermerajoykotlin.ui.scanner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.andoresudevs.enfermerajoykotlin.MainActivity
import com.andoresudevs.enfermerajoykotlin.databinding.FragmentScannerBinding
import com.google.zxing.integration.android.IntentIntegrator


class ScannerFragment : Fragment() {
    private lateinit var binding: FragmentScannerBinding
    private val viewModel: ScannerViewModel by viewModels()
    private lateinit var qrCodeLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentScannerBinding.inflate(inflater,container,false)

        val activity = requireActivity()
        if (activity is MainActivity) {
            activity.nfcScanViewState(true)
        }
        //QR
        qrCodeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val scannedJson: String? = data?.getStringExtra("SCAN_RESULT")
                // Use scannedText here...
                println("SCANNED TEXT: $scannedJson")
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnScanWithQr.setOnClickListener { startQrCodeScan() }
    }

    private fun startQrCodeScan() {
        val integrator = IntentIntegrator(activity)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Escanea el QR")
        integrator.setBeepEnabled(true)
        val intent: Intent? = integrator.createScanIntent()
        qrCodeLauncher.launch(intent)
    }

}