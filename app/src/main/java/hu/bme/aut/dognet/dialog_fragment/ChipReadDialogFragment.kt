package hu.bme.aut.dognet.dialog_fragment

import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.R
import kotlinx.android.synthetic.main.chipread_dialog_fragment.*

class ChipReadDialogFragment : DialogFragment() {

    //private lateinit var nfcAdapter: NfcAdapter
    //private lateinit var nfcPendingIntent: PendingIntent

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.chipread_dialog_fragment, LinearLayout(activity), false)

        val builder = Dialog(activity!!)
        builder.setContentView(view)

        //nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
        //nfcPendingIntent = PendingIntent.getActivity(activity, 0, Intent(activity, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)

        builder.btnStart.setOnClickListener {
            // TODO
        }

        builder.btnCancel.setOnClickListener {
            builder.onBackPressed()
        }

        return builder
    }

/*    private fun readChipNum() {
        if (activity!!.intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            val parcelableArray = activity!!.intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            parcelableArray?.forEach { it ->
                val ndefMsg = it as NdefMessage
                ndefMsg.records.forEach {
                    chipEditText.append("${String(it.payload)}\n")
                }
            }
        }
    }*/
}