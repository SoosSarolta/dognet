package hu.bme.aut.dognet.dialog_fragment.lost_n_found

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.R
import kotlinx.android.synthetic.main.found_and_lost_dialog_fragment.*

class FoundAndLostDialogFragment : DialogFragment() {

    private lateinit var builder: Dialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.found_and_lost_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        isCancelable = false

        builder.tvChipLost.text = arguments?.getString("chipNum")
        builder.tvBreedLost.text = arguments?.getString("breed")
        builder.tvSexLost.text = arguments?.getString("sex")
        builder.tvOwnerLost.text = arguments?.getString("ownerName")
        builder.tvPhoneLost.text = arguments?.getString("phoneNum")
        builder.tvPlaceLost.text = arguments?.getString("lastSeen")
        builder.tvExtraInfoLost.text = arguments?.getString("additionalInfo")

        if (builder.tvPhoneLost.text.isEmpty())
            builder.btnCall.isEnabled = false

        builder.btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(builder.tvPhoneLost.text.toString().trim())))
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
        val height = resources.getDimensionPixelSize(R.dimen.vet_form_popup_height)
        dialog!!.window!!.setLayout(width, height)
    }
}