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
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.MainActivity
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.lost_n_found.FoundMainFragment
import hu.bme.aut.dognet.trainer.TrainerDetailsFragment
import hu.bme.aut.dognet.util.Callback
import hu.bme.aut.dognet.vet.VetMainFragment
import kotlinx.android.synthetic.main.chipread_dialog_fragment.*
import org.ndeftools.Message
import org.ndeftools.wellknown.TextRecord

class ChipReadDialogFragment : DialogFragment() {

    private lateinit var builder: Dialog

    private var chipRead = false
    private var chipNum = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.chipread_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        isCancelable = false

        builder.btnStart.setOnClickListener {
            (activity as MainActivity).enableForegroundMode()
        }

        // TODO if parent is FoundMainFragment - stop searching for chip after 10 sec and call noChipFound
        builder.btnCancel.setOnClickListener {
            if (!chipRead) {
                dismiss()
            }
            else {
                val f = activity!!.supportFragmentManager.fragments[0].childFragmentManager.fragments[0]
                if (f is VetMainFragment) {
                    dismiss()
                    f.checkEntryAlreadyInDb(chipNum, object : Callback {
                            override fun onCallback() {
                                f.openVetDataForm()
                            }
                        })
                }

                else if (f is TrainerDetailsFragment) {
                    dismiss()
                    if(!f.checkEntryAlreadyInList(chipNum))
                        f.openTrainerDataForm()
                    else
                        Toast.makeText(activity, "Pet already added!", Toast.LENGTH_LONG).show()
                }

                else if (f is FoundMainFragment) {
                    dismiss()
                    f.chipRead(chipNum)
                }

                chipRead = false
            }
        }

        return builder
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (activity as MainActivity).disableForegroundMode()
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
                        if (validate()) {
                            chipNum = r.text
                            chipRead = true
                            builder.btnCancel.text = getString(R.string.ok)
                        }
                        else {
                            Toast.makeText(activity!!.applicationContext, "Empty chip number!", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun validate() = builder.chipEditText.text.isNotEmpty()

    private fun vibrate() {
        val vibe: Vibrator = activity!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibe.vibrate(500)
    }
}