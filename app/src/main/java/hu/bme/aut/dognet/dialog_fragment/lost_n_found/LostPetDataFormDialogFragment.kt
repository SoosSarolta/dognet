package hu.bme.aut.dognet.dialog_fragment.lost_n_found

import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.lost_n_found.LostMainFragment
import kotlinx.android.synthetic.main.data_form_lost_dialog_fragment.*

// TODO sex of pet to be chosen with checkbox
class LostPetDataFormDialogFragment : DialogFragment() {

    private lateinit var builder: Dialog

    private var chip: String = ""
    private var extra: String = ""
    private var lastSeen: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.data_form_lost_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        builder.submitFormBtn.setOnClickListener {
            if (validate()) {
                if (builder.chipNumEditText.text.isNotEmpty())
                    chip = builder.chipNumEditText.text.toString()

                if (builder.additionalInfoEditText.text.isNotEmpty())
                    extra = builder.additionalInfoEditText.text.toString()

                if (builder.lastSeenEditText.text.isNotEmpty())
                    lastSeen = builder.lastSeenEditText.text.toString()

                dismiss()

                val f = activity!!.supportFragmentManager.fragments[0].childFragmentManager.fragments[0]

                (f as LostMainFragment).setData(chip, builder.petNameEditText.text.toString(), builder.breedEditText.text.toString(),
                    builder.sexEditText.text.toString(), builder.ownerNameEditText.text.toString(), builder.phoneNumEditText.text.toString(),
                    lastSeen ,extra)
            }
            else
                // TODO nice error message instead
                Toast.makeText(activity!!.applicationContext, "Compulsory areas are blank!", Toast.LENGTH_LONG).show()
        }

        return builder
    }

    private fun validate() = builder.petNameEditText.text.isNotEmpty() && builder.breedEditText.text.isNotEmpty() &&
            builder.sexEditText.text.isNotEmpty() && builder.ownerNameEditText.text.isNotEmpty() &&
            builder.phoneNumEditText.text.isNotEmpty()
}