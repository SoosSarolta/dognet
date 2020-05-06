package hu.bme.aut.dognet.dialog_fragment.trainer

import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.trainer.TrainerMainFragment
import kotlinx.android.synthetic.main.new_or_old_training_dialog_fragment.*

class NewOrReviewTrainingDialogFragment : DialogFragment() {

    private lateinit var builder: Dialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.new_or_old_training_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        //isCancelable = false

        val f = activity!!.supportFragmentManager.fragments[0].childFragmentManager.fragments[0]

        builder.btnStartNew.setOnClickListener {
            if (f is TrainerMainFragment) {
                dismiss()
                f.startNewTrainingBtnPressed()
            }
        }

        builder.btnReview.setOnClickListener {
            if (f is TrainerMainFragment) {
                dismiss()
                f.reviewTrainingBtnPressed()
            }
        }

        return builder
    }

    override fun onResume() {
        super.onResume()

        val width = resources.getDimensionPixelSize(R.dimen.new_or_review_training_width)
        val height = resources.getDimensionPixelSize(R.dimen.new_or_review_training_height)
        dialog!!.window!!.setLayout(width, height)
    }
}