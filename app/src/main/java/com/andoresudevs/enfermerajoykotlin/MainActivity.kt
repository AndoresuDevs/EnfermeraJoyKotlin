package com.andoresudevs.enfermerajoykotlin

import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.andoresudevs.enfermerajoykotlin.databinding.ActivityMainBinding
import com.andoresudevs.enfermerajoykotlin.models.Doctor
import com.andoresudevs.enfermerajoykotlin.models.Hospital
import com.andoresudevs.enfermerajoykotlin.models.Treatment
import com.andoresudevs.enfermerajoykotlin.models.User
import com.andoresudevs.enfermerajoykotlin.ui.doctors.DoctorsFragment
import com.andoresudevs.enfermerajoykotlin.ui.hospitals.HospitalsFragment
import com.andoresudevs.enfermerajoykotlin.ui.menu.MenuFragment
import com.andoresudevs.enfermerajoykotlin.ui.profile.ProfileFragment
import com.andoresudevs.enfermerajoykotlin.ui.treatments.TreatmentFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var nfcSupported = true
    var nfcScanView=false

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val packageManager = this.packageManager
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            nfcSupported=false // NFC is not supported on this device
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if(nfcSupported){
            try {
                if(nfcScanView){ if (intent != null) readTextfromIntent(intent) }
            } catch (e: UnsupportedEncodingException) { e.printStackTrace() }
        }else{
            nfcNotSupported()
        }

    }

    private fun readTextfromIntent(intent: Intent){
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            //PENDING INTENT VALIDACION PARA EL SDK 33
            val rawMsgs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES,NdefMessage::class.java)
            }else{
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            }
            val msgs = ArrayList<NdefMessage>()
            if (rawMsgs != null) {
                for (i in rawMsgs.indices) {
                    msgs.add(rawMsgs[i] as NdefMessage)
                }
                val payload = msgs[0].records[0].payload
                val encoding = if ((payload[0].toInt() and 0x80) == 0) "UTF-8" else "UTF-16"
                val languageCodeLength = payload[0].toInt() and 0x3F
                val jsonFromNfcTag = String(payload, 1 + languageCodeLength, payload.size - 1 - languageCodeLength, Charset.forName(encoding))
                println("MENSAJE: $jsonFromNfcTag")

                openFragment(jsonFromNfcTag)

            }else{
                println("EL RAW MSGS NO TIENE CONTENIDO")
            }

        }
    }

    private fun openFragment(jsonObject:String){
        println("entro el openFragment")
        val partes = jsonObject.split("|")

        val bundle = Bundle()
        bundle.putString("json", partes[1])
        var fragment: Fragment? = null
        when (partes[0]) {
            "Perfil" ->fragment = ProfileFragment()
            "Trat" -> fragment = TreatmentFragment()
            "Doc" -> fragment = DoctorsFragment()
            "Hosp" -> {
                fragment = HospitalsFragment()
                println("FRAGMENTO HOSPITAL ENTRANTE")
            }
        }
        fragment?.arguments=bundle
        supportFragmentManager.commit {
            if (fragment != null) {
                replace(R.id.fragmentContainerView,fragment)
            }
            setReorderingAllowed(true)
            addToBackStack("name")
        }

    }

    private fun nfcNotSupported(){
        Toast.makeText(this,"NFC is not supported on this device", Toast.LENGTH_SHORT).show()
    }

    fun nfcScanViewState(active:Boolean){
        nfcScanView=active
    }
}


