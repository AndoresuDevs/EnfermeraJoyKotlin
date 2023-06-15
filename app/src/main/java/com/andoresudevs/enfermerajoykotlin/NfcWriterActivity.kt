package com.andoresudevs.enfermerajoykotlin

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.*
import android.nfc.tech.Ndef
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.andoresudevs.enfermerajoykotlin.databinding.ActivityMainBinding
import com.andoresudevs.enfermerajoykotlin.databinding.ActivityNfcWriterBinding
import java.io.IOException
import java.nio.charset.StandardCharsets

class NfcWriterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNfcWriterBinding
    var nfcSupported = true
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var pendingIntent: PendingIntent
    private lateinit var writingTagFilters: Array<IntentFilter>
    private var myTag: Tag? = null
    private var jsonObject=""
    private var tipo=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNfcWriterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        jsonObject= intent.getStringExtra("json").toString()
        tipo=intent.getIntExtra("tipo",0)

        val intent = Intent(this, javaClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE
        )
        val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT)
        writingTagFilters = arrayOf(tagDetected)

        binding.btnWriteOnNfcTag.setOnClickListener {
            if (nfcSupported) builderBotonNfc() else nfcNotSupported()
        }
        binding.btnBackToDetail.setOnClickListener { finish() }

    }

    private fun builderBotonNfc() {
        if (nfcAdapter.isEnabled) {
            try {
                if (myTag == null) {
                    binding.nfcTagStatus.text="Error al detectar la tarjeta NFC"
                } else {
                    val mensajeNFC = setJsonHeader(tipo)+jsonObject//MENSAJE QUE SE METE A LA TARJETA
                    write(mensajeNFC, myTag!!)
                    binding.nfcTagStatus.text="Escritura completada"
                }
            } catch (e: IOException) {
                binding.nfcTagStatus.text="Error al escribir"
            } catch (e: FormatException) {
                binding.nfcTagStatus.text="Error al escribir"
            }
        } else {
            binding.nfcTagStatus.text="NFC Desactivado"
        }
    }

    @Throws(IOException::class, FormatException::class)
    private fun write(text: String, tag: Tag) {
        val records = arrayOf(createRecord(text))
        val message = NdefMessage(records)
        val ndef = Ndef.get(tag)
        ndef.connect()
        ndef.writeNdefMessage(message)
        ndef.close()
    }

    private fun createRecord(text: String): NdefRecord {
        val lang = "en"
        val textBytes = text.toByteArray()
        val langBytes = lang.toByteArray(StandardCharsets.US_ASCII)
        val langLength = langBytes.size
        val textLength = textBytes.size
        val payload = ByteArray(1 + langLength + textLength)
        payload[0] = langLength.toByte()
        System.arraycopy(langBytes, 0, payload, 1, langLength)
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength)
        return NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, ByteArray(0), payload)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            binding.nfcTagStatus.text="NFC listo para escribir"
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }
    }

    override fun onPause() {
        super.onPause()
        if (nfcSupported) nfcAdapter.disableForegroundDispatch(this) else nfcNotSupported()
    }

    override fun onResume() {
        super.onResume()
        if (nfcSupported) nfcAdapter.enableForegroundDispatch(this, pendingIntent, writingTagFilters, null) else nfcNotSupported()
    }

    private fun nfcNotSupported(){
        Toast.makeText(this,"NFC is not supported on this device", Toast.LENGTH_SHORT).show()
    }

    private fun setJsonHeader(tipo:Int):String{
        return when(tipo){
            0->"Trat|"
            1->"Doc|"
            2->"Hosp|"
            3->"Perfil|"
            else->""
        }
    }
}