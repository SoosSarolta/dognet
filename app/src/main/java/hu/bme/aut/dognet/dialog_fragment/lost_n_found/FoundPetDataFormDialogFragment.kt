package hu.bme.aut.dognet.dialog_fragment.lost_n_found

import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.lost_n_found.FoundMainFragment
import kotlinx.android.synthetic.main.data_form_found_dialog_fragment.*

// TODO sex of pet to be chosen with checkbox
class FoundPetDataFormDialogFragment : DialogFragment() {

    private lateinit var builder: Dialog

    private var breed: String = ""
    private var sex: String = ""
    private var foundAt: String = ""
    private var extraInfo: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.data_form_found_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        builder.submitFormBtn.setOnClickListener {
            if (builder.breedEditText.text.isNotEmpty())
                breed = builder.breedEditText.text.toString()

            if (builder.sexEditText.text.isNotEmpty())
                sex = builder.sexEditText.text.toString()

            if (builder.foundAtEditText.text.isNotEmpty())
                foundAt = builder.foundAtEditText.text.toString()

            if (builder.additionalInfoEditText.text.isNotEmpty())
                extraInfo = builder.additionalInfoEditText.text.toString()

            dismiss()

            val f = activity!!.supportFragmentManager.fragments[0].childFragmentManager.fragments[0]

            (f as FoundMainFragment).setData(breed, sex, foundAt, extraInfo)
        }

        return builder
    }
}