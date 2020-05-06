package hu.bme.aut.dognet.dialog_fragment.trainer

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.trainer.TrainerDetailsFragment
import kotlinx.android.synthetic.main.data_form_trainer_dialog_fragment.*

class TrainerFormDialogFragment: DialogFragment() {

    private lateinit var builder: Dialog

    private var flag = false

    private var breed: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.data_form_trainer_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        //isCancelable = false

        val items = arrayOf("Beginner", "Intermediate", "Advanced", "Forest walk")
        val adapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_dropdown_item, items)
        builder.groupSpinner.adapter = adapter

        val f = activity!!.supportFragmentManager.fragments[0].childFragmentManager.fragments[0]
        builder.groupSpinner.setSelection(adapter.getPosition((f as TrainerDetailsFragment).getGroup()))

        builder.submitFormBtn.setOnClickListener {
            validate()

            if (flag) {
                if (builder.breedEditText.text.isNotEmpty())
                    breed = builder.breedEditText.text.toString()

                dismiss()

                f.setData(builder.petNameEditText.text.toString(), breed, builder.ownerNameEditText.text.toString(),
                    builder.phoneNumEditText.text.toString(), builder.groupSpinner.selectedItem.toString()
                )
            }
        }

        return builder
    }

    override fun onResume() {
        super.onResume()

        val width = resources.getDimensionPixelSize(R.dimen.data_form_found_popup_width)
        val height = resources.getDimensionPixelSize(R.dimen.trainer_form_popup_height)
        dialog!!.window!!.setLayout(width, height)
    }

    private fun validate() {
        if (builder.petNameEditText.text.isEmpty()) {
            flag = false
            builder.petNameEditText.error = "This is a compulsory area!"
        }

        if (builder.ownerNameEditText.text.isEmpty()) {
            flag = false
            builder.ownerNameEditText.error = "This is a compulsory area!"
        }

        if (builder.phoneNumEditText.text.isEmpty()) {
            flag = false
            builder.phoneNumEditText.error = "This is a compulsory area!"
        }

        if (builder.petNameEditText.text.isNotEmpty() && builder.ownerNameEditText.text.isNotEmpty()
            && builder.phoneNumEditText.text.isNotEmpty())
            flag = true
    }
}