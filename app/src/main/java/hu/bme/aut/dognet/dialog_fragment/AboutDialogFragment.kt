package hu.bme.aut.dognet.dialog_fragment

import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.R
import kotlinx.android.synthetic.main.about_dialog_fragment.*

class AboutDialogFragment : DialogFragment() {

    private lateinit var builder: Dialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.about_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        builder.okBtn.setOnClickListener {
            dismiss()
        }

        return builder
    }

    override fun onResume() {
        super.onResume()

        val width = resources.getDimensionPixelSize(R.dimen.about_popup_width)
        val height = resources.getDimensionPixelSize(R.dimen.about_popup_height)
        dialog!!.window!!.setLayout(width, height)
    }
}