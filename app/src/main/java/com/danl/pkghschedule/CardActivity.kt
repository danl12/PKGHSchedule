package com.danl.pkghschedule

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.acitivity_card.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CardActivity : BaseActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_card)

        setSupportActionBar(toolbar)
        if (callingActivity != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "Устройство не поддерживает NFC.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        if (!nfcAdapter!!.isEnabled) {
            Toast.makeText(this, "NFC выключен.", Toast.LENGTH_LONG).show()
        }
        pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, javaClass), 0)
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter!!.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter!!.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            try {
                val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
                val mifareClassic = MifareClassic.get(tag)

                mifareClassic.connect()
                mifareClassic.authenticateSectorWithKeyA(8, byteArrayOf(38, -105, 62, -89, 67, 33))
                val bytes = mifareClassic.readBlock(mifareClassic.sectorToBlock(8))
                dayLeftTextView.text = getString(
                    R.string.info_card,
                    readDaysLeft(bytes.copyOfRange(10, 15)),
                    readDate(bytes.copyOfRange(10, 15))
                )
                mifareClassic.close()
            } catch (exception: Exception) {
                Toast.makeText(this, "Не удалось считать БСК.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun readDaysLeft(bytes: ByteArray): String {
        val currentTimeInMillis = Calendar.getInstance().timeInMillis
        val calendar = Calendar.getInstance()
        calendar.set(bytes[0] + 2000, bytes[1] - 1, bytes[2] + 1)
        return TimeUnit.MILLISECONDS.toDays(calendar.timeInMillis - currentTimeInMillis).toString()
    }

    private fun readDate(bytes: ByteArray): String {
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru"))
        val calendar = Calendar.getInstance()
        calendar.set(
            bytes[0] + 2000,
            bytes[1] - 1,
            bytes[2].toInt(),
            bytes[3] + 3,
            bytes[4] + 1
        )
        return simpleDateFormat.format(calendar.time)
    }

}