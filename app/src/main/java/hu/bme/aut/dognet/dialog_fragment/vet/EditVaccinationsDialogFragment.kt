package hu.bme.aut.dognet.dialog_fragment.vet

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.vet.VetDetailsFragment
import kotlinx.android.synthetic.main.edit_vacc_dialog_fragment.*
import java.util.*
import kotlin.collections.ArrayList

class EditVaccinationsDialogFragment : DialogFragment() {

    private lateinit var builder: Dialog

    private var flag = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.edit_vacc_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        isCancelable = false

        builder.etVaccDate.setOnClickListener {
            val currentDate: Calendar = Calendar.getInstance()
            val currentYear = currentDate.get(Calendar.YEAR)
            val currentMonth = currentDate.get(Calendar.MONTH)
            val currentDay = currentDate.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { _, myear, mmonth, mday ->
                builder.etVaccDate.setText(getString(R.string.date_format, mday, mmonth + 1, myear))
            }, currentYear, currentMonth, currentDay)

            datePicker.datePicker.maxDate = currentDate.timeInMillis

            datePicker.show()
        }

        builder.btnSubmitVacc.setOnClickListener {
            validate()

            if (flag) {
                val vaccList: MutableList<String> = ArrayList()
                vaccList.add(builder.etVaccName.text.toString() + " : " + builder.etVaccDate.text.toString())

                dismiss()

                val f = activity!!.supportFragmentManager.fragments[0].childFragmentManager.fragments[0]

                if (f is VetDetailsFragment)
                    f.setVaccination(vaccList)
            }
        }

        builder.btnCancelVacc.setOnClickListener {
            dismiss()
        }

        return builder
    }

    private fun validate() {
        if (builder.etVaccName.text.isEmpty()) {
            flag = false
            builder.etVaccName.error = "This is a compulsory area!"
        }

        if (builder.etVaccDate.text.isEmpty()) {
            flag = false
            builder.etVaccDate.error = "This is a compulsory area!"
        }

        if (builder.etVaccName.text.isNotEmpty() && builder.etVaccDate.text.isNotEmpty())
            flag = true
    }
}