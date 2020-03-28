package hu.bme.aut.dognet.dialog_fragment

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.nfc.NdefMessage
import android.os.Bundle
import android.os.Parcelable
import android.os.Vibrator
import android.util.Log
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.MainActivity
import hu.bme.aut.dognet.R
import kotlinx.android.synthetic.main.chipread_dialog_fragment.*
import org.ndeftools.Message
import org.ndeftools.wellknown.TextRecord

class ChipReadDialogFragment : DialogFragment() {

    private lateinit var builder: Dialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.chipread_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        builder.btnStart.setOnClickListener {
            (parentFragment!!.activity as MainActivity).enableForegroundMode()
        }

        builder.btnCancel.setOnClickListener {
            builder.onBackPressed()
        }

        return builder
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (parentFragment!!.activity as MainActivity).disableForegroundMode()
    }

    fun processNFC(messages: Array<Parcelable>?) {
        Log.d("ChipReaderFragment", "NFC action started!")

        if (messages != null) {
            Log.d("ChipReaderFragment", "Found " + messages.size + " NDEF messages")

            vibrate()

            for (m in messages) {
                val records = Message(m as NdefMessage)

                for (r in records) {
                    if (r is TextRecord) {
                        Log.d("Main", "Message: " + r.text)
                        builder.chipEditText.setText(r.text)
                    }
                }
            }
        }
    }

    private fun vibrate() {
        val vibe: Vibrator = activity!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibe.vibrate(500)
    }
}