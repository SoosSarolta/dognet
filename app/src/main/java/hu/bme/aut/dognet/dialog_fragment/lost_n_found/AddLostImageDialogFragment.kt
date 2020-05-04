package hu.bme.aut.dognet.dialog_fragment.lost_n_found

import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.lost_n_found.LostMainFragment
import kotlinx.android.synthetic.main.add_lost_image_dialog_fragment.*

class AddLostImageDialogFragment : DialogFragment () {

    private lateinit var builder: Dialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.add_lost_image_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        val f = activity!!.supportFragmentManager.fragments[0].childFragmentManager.fragments[0]

        builder.btnTakePhoto.setOnClickListener {
            if (f is LostMainFragment) {
                dismiss()
                f.takePhotoBtnClicked()
            }
        }

        builder.btnChoosePhoto.setOnClickListener {
            if (f is LostMainFragment) {
                dismiss()
                f.choosePhotoBtnClicked()
            }
        }

        builder.btnCancel.setOnClickListener {
            if (f is LostMainFragment) {
                dismiss()
                f.noPhotoBtnClicked()
            }
        }

        return builder
    }
}