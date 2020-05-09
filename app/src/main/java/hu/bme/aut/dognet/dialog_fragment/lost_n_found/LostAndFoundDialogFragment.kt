package hu.bme.aut.dognet.dialog_fragment.lost_n_found

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.R
import kotlinx.android.synthetic.main.lost_and_found_dialog_fragment.*

class LostAndFoundDialogFragment : DialogFragment() {

    private lateinit var builder: Dialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.lost_and_found_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        isCancelable = false

        builder.tvChipFound.text = arguments?.getString("chipNum")
        builder.tvSexFound.text = arguments?.getString("sex")
        builder.tvBreedFound.text = arguments?.getString("breed")
        builder.tvPhoneFound.text = arguments?.getString("phone")
        builder.tvPlaceFound.text = arguments?.getString("foundAt")
        builder.tvExtraInfoFound.text = arguments?.getString("additionalInfo")

        if (builder.tvPhoneFound.text.isEmpty())
            builder.btnCall.isEnabled = false

        builder.btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(builder.tvPhoneFound.text.toString().trim())))
            startActivity(intent)
        }

        builder.btnCancel.setOnClickListener {
            dismiss()
        }

        return builder
    }

    override fun onResume() {
        super.onResume()

        val width = resources.getDimensionPixelSize(R.dimen.data_form_found_popup_width)
        val height = resources.getDimensionPixelSize(R.dimen.lost_and_found_popup_height)
        dialog!!.window!!.setLayout(width, height)
    }
}