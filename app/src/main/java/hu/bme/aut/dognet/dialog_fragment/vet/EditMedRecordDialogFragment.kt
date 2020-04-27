package hu.bme.aut.dognet.dialog_fragment.vet

import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.vet.VetDetailsFragment
import kotlinx.android.synthetic.main.edit_medrec_dialog_fragment.*

class EditMedRecordDialogFragment : DialogFragment() {

    private lateinit var builder: Dialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.edit_medrec_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        builder.btnSubmitMedRec.setOnClickListener {
            if (validate()) {
                val medRecList: MutableList<String> = ArrayList()
                medRecList.add(builder.etMedRec.text.toString())

                dismiss()

                val f = activity!!.supportFragmentManager.fragments[0].childFragmentManager.fragments[0]

                if (f is VetDetailsFragment)
                    f.setMedRecord(medRecList)
            }
            else {
                builder.onBackPressed()
            }
        }

        builder.btnCancelMedRec.setOnClickListener {
            builder.onBackPressed()
        }

        return builder
    }

    private fun validate() = builder.etMedRec.text.isNotEmpty()
}