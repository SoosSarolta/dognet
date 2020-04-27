package hu.bme.aut.dognet.dialog_fragment.vet

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.vet.VetDetailsFragment
import kotlinx.android.synthetic.main.edit_vacc_dialog_fragment.*
import java.util.*
import kotlin.collections.HashMap

class EditVaccinationsDialogFragment : DialogFragment() {

    private lateinit var builder: Dialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.edit_vacc_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        builder.etVaccDate.setOnClickListener {
            val currentDate: Calendar = Calendar.getInstance()
            val currentYear = currentDate.get(Calendar.YEAR)
            val currentMonth = currentDate.get(Calendar.MONTH)
            val currentDay = currentDate.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { _, myear, mmonth, mday ->
                builder.etVaccDate.setText("" + mday + " - " + (mmonth + 1) + " - " + myear)
            }, currentYear, currentMonth, currentDay)

            datePicker.datePicker.maxDate = currentDate.timeInMillis

            datePicker.show()
        }

        builder.btnSubmitVacc.setOnClickListener {
            if (validate()) {
                val vaccMap: MutableMap<String, String> = HashMap()
                vaccMap[builder.etVaccName.text.toString()] = builder.etVaccDate.text.toString()

                dismiss()

                val f =
                    activity!!.supportFragmentManager.fragments[0].childFragmentManager.fragments[0]

                if (f is VetDetailsFragment)
                    f.setVaccination(vaccMap)
            }
            else {
                // TODO nice error message instead
                Toast.makeText(activity, "Please fill in both boxes!", Toast.LENGTH_LONG).show()
            }
        }

        builder.btnCancelVacc.setOnClickListener {
            builder.onBackPressed()
        }

        return builder
    }

    private fun validate() = builder.etVaccName.text.isNotEmpty() && builder.etVaccDate.text.isNotEmpty()
}