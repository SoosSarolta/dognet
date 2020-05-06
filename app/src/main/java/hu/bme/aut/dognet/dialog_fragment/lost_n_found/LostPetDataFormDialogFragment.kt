package hu.bme.aut.dognet.dialog_fragment.lost_n_found

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.lost_n_found.LostMainFragment
import kotlinx.android.synthetic.main.data_form_lost_dialog_fragment.*

class LostPetDataFormDialogFragment : DialogFragment() {

    private lateinit var builder: Dialog

    private var chip: String = ""
    private var extra: String = ""
    private var lastSeen: String = ""

    private var flag = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.data_form_lost_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        //isCancelable = false

        val items = arrayOf("Male", "Female")
        val adapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_dropdown_item, items)
        builder.sexSpinner.adapter = adapter

        builder.submitFormBtn.setOnClickListener {
            validate()

            if (flag) {
                if (builder.chipNumEditText.text.isNotEmpty())
                    chip = builder.chipNumEditText.text.toString()

                if (builder.additionalInfoEditText.text.isNotEmpty())
                    extra = builder.additionalInfoEditText.text.toString()

                if (builder.lastSeenEditText.text.isNotEmpty())
                    lastSeen = builder.lastSeenEditText.text.toString()

                dismiss()

                val f = activity!!.supportFragmentManager.fragments[0].childFragmentManager.fragments[0]

                (f as LostMainFragment).setData(chip, builder.petNameEditText.text.toString(), builder.breedEditText.text.toString(),
                    builder.sexSpinner.selectedItem.toString(), builder.ownerNameEditText.text.toString(),
                    builder.phoneNumEditText.text.toString(), lastSeen, extra)
            }
        }

        return builder
    }

    override fun onResume() {
        super.onResume()

        val width = resources.getDimensionPixelSize(R.dimen.data_form_found_popup_width)
        val height = resources.getDimensionPixelSize(R.dimen.data_form_lost_popup_height)
        dialog!!.window!!.setLayout(width, height)
    }

    private fun validate() {
        if (builder.petNameEditText.text.isEmpty()) {
            flag = false
            builder.petNameEditText.error = "This is a compulsory area!"
        }

        if (builder.breedEditText.text.isEmpty()) {
            flag = false
            builder.breedEditText.error = "This is a compulsory area!"
        }

        if (builder.ownerNameEditText.text.isEmpty()) {
            flag = false
            builder.ownerNameEditText.error = "This is a compulsory area!"
        }

        if (builder.phoneNumEditText.text.isEmpty()) {
            flag = false
            builder.phoneNumEditText.error = "This is a compulsory area!"
        }

        if (builder.petNameEditText.text.isNotEmpty() && builder.breedEditText.text.isNotEmpty() &&
                builder.ownerNameEditText.text.isNotEmpty() && builder.phoneNumEditText.text.isNotEmpty())
            flag = true
    }
}