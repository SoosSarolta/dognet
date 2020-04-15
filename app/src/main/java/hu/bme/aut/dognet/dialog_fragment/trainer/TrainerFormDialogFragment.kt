package hu.bme.aut.dognet.dialog_fragment.trainer

import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.trainer.TrainerMainFragment
import kotlinx.android.synthetic.main.data_form_trainer_dialog_fragment.*

class TrainerFormDialogFragment: DialogFragment() {

    private lateinit var builder: Dialog

    private var breed: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.data_form_trainer_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        builder.submitFormBtn.setOnClickListener {
            if (validate()) {
                if (builder.breedEditText.text.isNotEmpty())
                    breed = builder.breedEditText.text.toString()

                dismiss()

                val f = activity!!.supportFragmentManager.fragments[0].childFragmentManager.fragments[0]

                (f as TrainerMainFragment).setData(builder.petNameEditText.text.toString(), breed,
                    builder.ownerNameEditText.text.toString(), builder.phoneNumEditText.text.toString(),
                    builder.groupEditText.text.toString())
            }
            else
                Toast.makeText(activity!!.applicationContext, "Compulsory areas are blank!", Toast.LENGTH_LONG).show()

        }

        return builder
    }

    private fun validate() = builder.petNameEditText.text.isNotEmpty() && builder.ownerNameEditText.text.isNotEmpty()
            && builder.phoneNumEditText.text.isNotEmpty() && builder.groupEditText.text.isNotEmpty()
}