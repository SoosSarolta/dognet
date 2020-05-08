package hu.bme.aut.dognet.dialog_fragment.vet

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.vet.VetMainFragment
import kotlinx.android.synthetic.main.data_form_vet_dialog_fragment.*
import java.util.*

class VetFormDialogFragment : DialogFragment() {

    private lateinit var builder: Dialog

    private var breed: String = ""
    private var dob: String = ""

    private var flag = false

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.data_form_vet_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        //isCancelable = false

        val items = arrayOf("Male", "Female")
        val adapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_dropdown_item, items)
        builder.sexSpinner.adapter = adapter

        builder.dobEditText.setOnClickListener {
            val currentDate: Calendar = Calendar.getInstance()
            val currentYear = currentDate.get(Calendar.YEAR)
            val currentMonth = currentDate.get(Calendar.MONTH)
            val currentDay = currentDate.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { _, myear, mmonth, mday ->
                builder.dobEditText.setText(getString(R.string.date_format, mday, mmonth + 1, myear))
            }, currentYear, currentMonth, currentDay)

            datePicker.datePicker.maxDate = currentDate.timeInMillis

            datePicker.show()
        }

        builder.submitFormBtn.setOnClickListener {
            validate()

            if (flag) {
                if (builder.breedEditText.text.isNotEmpty())
                     breed = builder.breedEditText.text.toString()
                if (builder.dobEditText.text.isNotEmpty())
                    dob = builder.dobEditText.text.toString()

                dismiss()

                val f = activity!!.supportFragmentManager.fragments[0].childFragmentManager.fragments[0]

                (f as VetMainFragment).setData(builder.petNameEditText.text.toString(), breed,
                    builder.sexSpinner.selectedItem.toString(), dob, builder.ownerNameEditText.text.toString(),
                    builder.addressEditText.text.toString(), builder.phoneNumEditText.text.toString())
            }
        }

        return builder
    }

    override fun onResume() {
        super.onResume()

        val width = resources.getDimensionPixelSize(R.dimen.data_form_found_popup_width)
        val height = resources.getDimensionPixelSize(R.dimen.vet_form_popup_height)
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

        if (builder.addressEditText.text.isEmpty()) {
            flag = false
            builder.addressEditText.error = "This is a compulsory area!"
        }

        if (builder.phoneNumEditText.text.isEmpty()) {
            flag = false
            builder.phoneNumEditText.error = "This is a compulsory area!"
        }

        if (builder.petNameEditText.text.isNotEmpty() && builder.ownerNameEditText.text.isNotEmpty()
            && builder.addressEditText.text.isNotEmpty() && builder.phoneNumEditText.text.isNotEmpty())
            flag = true
    }
}