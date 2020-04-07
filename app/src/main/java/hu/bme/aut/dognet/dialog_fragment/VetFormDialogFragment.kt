package hu.bme.aut.dognet.dialog_fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.vet.VetMainFragment
import kotlinx.android.synthetic.main.data_form_vet_dialog_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class VetFormDialogFragment : DialogFragment() {

    private lateinit var builder: Dialog

    private var breed: String = ""
    private var dob: String = ""

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.data_form_vet_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        builder.dobEditText.setOnClickListener {
            val currentDate: Calendar = Calendar.getInstance()
            val currentYear = currentDate.get(Calendar.YEAR)
            val currentMonth = currentDate.get(Calendar.MONTH)
            val currentDay = currentDate.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { _, myear, mmonth, mday ->
                builder.dobEditText.setText("" + mday + " - " + mmonth + " - " + myear)
            }, currentYear, currentMonth, currentDay)

            datePicker.show()
        }

        builder.submitFormBtn.setOnClickListener {
            if (validate()) {
                if (builder.breedEditText.text.isNotEmpty())
                     breed = builder.breedEditText.text.toString()
                if (builder.dobEditText.text.isNotEmpty())
                    dob = builder.dobEditText.text.toString()

                dismiss() // ???

                val f = activity!!.supportFragmentManager.fragments[0].childFragmentManager.fragments[0]

                (f as VetMainFragment).setData(builder.petNameEditText.text.toString(), breed,
                    builder.sexEditText.text.toString(), dob, builder.ownerNameEditText.text.toString(),
                    builder.addressEditText.text.toString(), builder.phoneNumEditText.text.toString())
            }
            else
                Toast.makeText(activity!!.applicationContext, "Compulsory areas are blank!", Toast.LENGTH_LONG).show()
        }

        return builder
    }

    private fun validate() = builder.petNameEditText.text.isNotEmpty() && builder.ownerNameEditText.text.isNotEmpty()
            && builder.sexEditText.text.isNotEmpty() && builder.addressEditText.text.isNotEmpty()
            && builder.phoneNumEditText.text.isNotEmpty()
}