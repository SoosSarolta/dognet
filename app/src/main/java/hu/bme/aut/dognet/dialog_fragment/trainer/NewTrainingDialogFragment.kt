package hu.bme.aut.dognet.dialog_fragment.trainer

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.trainer.TrainerMainFragment
import hu.bme.aut.dognet.util.Callback
import kotlinx.android.synthetic.main.new_training_dialog_fragment.*
import java.util.*

// TODO choose group with radio button
class NewTrainingDialogFragment : DialogFragment() {

    private lateinit var builder: Dialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.new_training_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        builder.etTrainingDate.setOnClickListener {
            val currentDate: Calendar = Calendar.getInstance()
            val currentYear = currentDate.get(Calendar.YEAR)
            val currentMonth = currentDate.get(Calendar.MONTH)
            val currentDay = currentDate.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { _, myear, mmonth, mday ->
                builder.etTrainingDate.setText("" + mday + " - " + (mmonth + 1) + " - " + myear)
            }, currentYear, currentMonth, currentDay)

            datePicker.show()
        }

        builder.btnOkTraining.setOnClickListener {
            val f = activity!!.supportFragmentManager.fragments[0].childFragmentManager.fragments[0]

            // TODO notify user with toast if training is alredy in DB
            if (f is TrainerMainFragment) {
                if (validate()) {
                    val trainingDate = builder.etTrainingDate.text.toString()
                    val trainingGroup = builder.etGroup.text.toString()

                    dismiss()

                    f.checkEntryAlreadyInDb(trainingDate, object : Callback {
                        override fun onCallback() {
                            f.setData(trainingDate, trainingGroup)
                        }
                    })
                }
                else
                    // TODO nice error message instead
                    Toast.makeText(activity!!.applicationContext, "Compulsory areas are blank!", Toast.LENGTH_LONG).show()
            }
        }

        builder.btnCancelTraining.setOnClickListener {
            builder.onBackPressed()
        }

        return builder
    }

    private fun validate() = builder.etTrainingDate.text.isNotEmpty() && builder.etGroup.text.isNotEmpty()
}