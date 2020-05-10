package hu.bme.aut.dognet.vet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hu.bme.aut.dognet.MainActivity
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.dialog_fragment.vet.EditMedRecordDialogFragment
import hu.bme.aut.dognet.dialog_fragment.vet.EditVaccinationsDialogFragment
import hu.bme.aut.dognet.util.VET_FIREBASE_ENTRY
import kotlinx.android.synthetic.main.fragment_vet_details.*

class VetDetailsFragment : Fragment() {

    private val args: VetDetailsFragmentArgs by navArgs()

    private var vaccinations: MutableList<String> = ArrayList()
    private var records: MutableList<String> = ArrayList()

    private lateinit var db: FirebaseFirestore
    private lateinit var adapterVacc: ArrayAdapter<String>
    private lateinit var adapterMed: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vet_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = Firebase.firestore

        tvDetailsChip.text = args.itemChipNum.toString()
        tvDetailsPetName.text = args.petName.toString()

        if (args.breed.toString() != "null")
            tvDetailsBreed.text = args.breed.toString()
        else
            tvDetailsBreed.text = getString(R.string.unkown)

        tvDetailsSex.text = args.sex.toString()

        if (args.dob.toString() != "null")
            tvDetailsDob.text = args.dob.toString()
        else
            tvDetailsDob.text = getString(R.string.unkown)

        tvDetailsOwnerName.text = args.ownerName.toString()
        tvDetailsAddress.text = args.ownerAddress.toString()
        tvDetailsPhone.text = args.ownerPhone.toString()

        for (x in args.vaccinations)
            vaccinations.add(x)

        for (x in args.medRecords)
            records.add(x)

        adapterVacc = ArrayAdapter(activity!!, android.R.layout.simple_list_item_1, vaccinations)
        vaccListView.adapter = adapterVacc

        adapterMed = ArrayAdapter(activity!!, android.R.layout.simple_list_item_1, records)
        medRecListView.adapter = adapterMed

        btnEditVaccinations.setOnClickListener {
            val dialogFragment = EditVaccinationsDialogFragment()
            (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "vacc_dialog_vet") }
        }

        btnEditMedRecord.setOnClickListener {
            val dialogFragment = EditMedRecordDialogFragment()

            val myArgs = Bundle()
            myArgs.putString("chipNum", args.itemChipNum.toString())
            dialogFragment.arguments = myArgs

            (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "med_dialog_vet") }
        }
    }

    fun setVaccination(vacc: MutableList<String>) {
        vacc.forEach { vaccinations.add(it) }
        adapterVacc.notifyDataSetChanged()

        for (x in args.vaccinations)
            vacc.add(x)

        val ref = db.collection(VET_FIREBASE_ENTRY).document(args.itemChipNum.toString())
        ref.update("vaccinations", vaccinations)
    }

    fun setMedRecord(rec: MutableList<String>) {
        for (r in rec)
            records.add(r)
        adapterMed.notifyDataSetChanged()

        for (x in args.medRecords)
            rec.add(x)

        val ref = db.collection(VET_FIREBASE_ENTRY).document(args.itemChipNum.toString())
        ref.update("medRecord", records)
    }
}
