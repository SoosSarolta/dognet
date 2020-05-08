package hu.bme.aut.dognet.dialog_fragment.trainer

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.trainer.TrainerMainFragment
import hu.bme.aut.dognet.util.Callback
import kotlinx.android.synthetic.main.new_training_dialog_fragment.*
import java.util.*

class NewTrainingDialogFragment : DialogFragment() {

    private lateinit var builder: Dialog

    private var flag = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.new_training_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        isCancelable = false

        val items = arrayOf("Beginner", "Intermediate", "Advanced", "Forest walk")
        val adapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_dropdown_item, items)
        builder.groupSpinner.adapter = adapter

        builder.etTrainingDate.setOnClickListener {
            val currentDate: Calendar = Calendar.getInstance()
            val currentYear = currentDate.get(Calendar.YEAR)
            val currentMonth = currentDate.get(Calendar.MONTH)
            val currentDay = currentDate.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { _, myear, mmonth, mday ->
                builder.etTrainingDate.setText(getString(R.string.date_format, mday, mmonth + 1, myear))
            }, currentYear, currentMonth, currentDay)

            datePicker.show()
        }

        val f = activity!!.supportFragmentManager.fragments[0].childFragmentManager.fragments[0]

        builder.btnOkTraining.setOnClickListener {
            if (f is TrainerMainFragment) {
                validate()

                if (flag) {
                    val trainingDate = builder.etTrainingDate.text.toString()
                    val trainingGroup = builder.groupSpinner.selectedItem.toString()

                    dismiss()

                    f.checkEntryAlreadyInDb(trainingDate, object : Callback {
                        override fun onCallback() {
                            f.setData(trainingDate, trainingGroup)
                        }
                    })
                }
            }
        }

        builder.btnCancelTraining.setOnClickListener {
            if (f is TrainerMainFragment) {
                dismiss()

                f.reviewTrainingBtnPressed()
            }
        }

        return builder
    }

    private fun validate() {
        if (builder.etTrainingDate.text.isEmpty()) {
            flag = false
            builder.etTrainingDate.error = "This is a compulsory area!"
        }
        else
            flag = true
    }
}