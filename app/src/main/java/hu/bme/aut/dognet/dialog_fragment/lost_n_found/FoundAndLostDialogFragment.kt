package hu.bme.aut.dognet.dialog_fragment.lost_n_found

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.util.FOUND_FIREBASE_ENTRY
import hu.bme.aut.dognet.util.LOST_FIREBASE_ENTRY
import kotlinx.android.synthetic.main.found_and_lost_dialog_fragment.*

class FoundAndLostDialogFragment : DialogFragment() {

    private lateinit var db: FirebaseFirestore

    private lateinit var builder: Dialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.found_and_lost_dialog_fragment, LinearLayout(activity), false)

        builder = Dialog(activity!!)
        builder.setContentView(view)

        db = Firebase.firestore

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
            if (arguments?.getString("chipNum").equals(" - ")) {
                db.collection(LOST_FIREBASE_ENTRY).document("no chip - " + arguments?.getString("sex") + " < > " + arguments?.getString("breed")).delete()
                    .addOnSuccessListener {
                        Log.d("FoundAndLostDialog", "Document deleted!")
                    }
                    .addOnFailureListener {
                        Log.d("FoundAndLostDialog", "Error deleting document!")
                    }
            }
            else {
                db.collection(LOST_FIREBASE_ENTRY).document(arguments?.getString(("chipNum"))!!).delete()
                    .addOnSuccessListener {
                        Log.d("FoundAndLostDialog", "Document deleted!")
                    }
                    .addOnFailureListener {
                        Log.d("FoundAndLostDialog", "Error deleting document!")
                    }
            }

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