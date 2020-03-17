package hu.bme.aut.dognet.dialog_fragment

import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.R

class ChipReadDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.chipread_dialog_fragment, LinearLayout(activity), false)

        val builder = Dialog(activity!!)
        builder.setContentView(view)
        return builder
    }
}