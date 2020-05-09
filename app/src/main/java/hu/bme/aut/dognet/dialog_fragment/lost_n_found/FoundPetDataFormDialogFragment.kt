package hu.bme.aut.dognet.dialog_fragment.lost_n_found

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.lost_n_found.FoundMainFragment
import kotlinx.android.synthetic.main.data_form_found_dialog_fragment.*

class FoundPetDataFormDialogFragment : DialogFragment() {

    private lateinit var builder: Dialog

    private var breed: String = ""
    private var sex: String = ""
    private var phone: String = ""
    private var foundAt: String = ""
    private var extraInfo: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.data_form_found_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        //isCancelable = false

        val items = arrayOf("Male", "Female")
        val adapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_dropdown_item, items)
        builder.sexSpinner.adapter = adapter

        builder.submitFormBtn.setOnClickListener {
            if (builder.breedEditText.text.isNotEmpty())
                breed = builder.breedEditText.text.toString()

            sex = builder.sexSpinner.selectedItem.toString()

            if (builder.foundAtEditText.text.isNotEmpty())
                foundAt = builder.foundAtEditText.text.toString()

            if (builder.additionalInfoEditText.text.isNotEmpty())
                extraInfo = builder.additionalInfoEditText.text.toString()

            phone = builder.phoneNumEditText.text.toString()

            dismiss()

            val f = activity!!.supportFragmentManager.fragments[0].childFragmentManager.fragments[0]

            (f as FoundMainFragment).setData(breed, sex, phone, foundAt, extraInfo)
        }

        return builder
    }

    override fun onResume() {
        super.onResume()

        val width = resources.getDimensionPixelSize(R.dimen.data_form_found_popup_width)
        val height = resources.getDimensionPixelSize(R.dimen.data_form_found_popup_height)
        dialog!!.window!!.setLayout(width, height)
    }
}