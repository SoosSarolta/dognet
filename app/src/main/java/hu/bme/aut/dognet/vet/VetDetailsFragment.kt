package hu.bme.aut.dognet.vet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.dialog_fragment.vet.EditMedRecordDialogFragment
import hu.bme.aut.dognet.dialog_fragment.vet.EditVaccinationsDialogFragment
import hu.bme.aut.dognet.util.DB
import hu.bme.aut.dognet.util.VET_FIREBASE_ENTRY
import kotlinx.android.synthetic.main.fragment_vet_details.*

// TODO edittexts show only one line upon loading
// TODO replace deprecated fragment manager calls
class VetDetailsFragment : Fragment() {

    private val args: VetDetailsFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vet_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvDetailsChip.text = args.itemChipNum.toString()
        tvDetailsPetName.text = args.petName.toString()
        tvDetailsBreed.text = args.breed.toString()
        tvDetailsSex.text = args.sex.toString()
        tvDetailsDob.text = args.dob.toString()
        tvDetailsOwnerName.text = args.ownerName.toString()
        tvDetailsAddress.text = args.ownerAddress.toString()
        tvDetailsPhone.text = args.ownerPhone.toString()

        for (x in args.vaccNames.indices)
            etDetailsVaccinations.setText(args.vaccNames[x] + " - " + args.vaccDates[x] + "\n")

        for (x in args.medRecords)
            etDetailsMedRecord.setText(x + "\n")

        btnEditVaccinations.setOnClickListener {
            val dialogFragment = EditVaccinationsDialogFragment()
            fragmentManager?.let { dialogFragment.show(it, "vacc_dialog_vet") }
        }

        btnEditMedRecord.setOnClickListener {
            val dialogFragment = EditMedRecordDialogFragment()

            val myArgs = Bundle()
            myArgs.putString("chipNum", args.itemChipNum.toString())
            dialogFragment.arguments = myArgs

            fragmentManager?.let { dialogFragment.show(it, "med_dialog_vet") }
        }
    }

    fun setVaccination(vacc: MutableMap<String, String>) {
        val ref = DB.child(VET_FIREBASE_ENTRY).child(args.itemChipNum.toString())
        val update: MutableMap<String, MutableMap<String, String>> = HashMap()

        vacc.forEach {  etDetailsVaccinations.setText(etDetailsVaccinations.text.toString() + it.key + " - " + it.value + "\n") }

        for (x in args.vaccNames.indices)
            vacc[args.vaccNames[x]] = args.vaccDates[x]

        update["vaccinations"] = vacc

        ref.updateChildren(update as Map<String, Any>)

        // TODO reach VetMainFragment
        /*val f = parentFragment?.parentFragmentManager?.fragments

        if (f is VetMainFragment)
            f.setVaccinations(vacc)*/
    }

    fun setMedRecord(rec: MutableList<String>) {
        val ref = DB.child(VET_FIREBASE_ENTRY).child(args.itemChipNum.toString())
        val update: MutableMap<String, MutableList<String>> = HashMap()

        for (r in rec)
            etDetailsMedRecord.setText(etDetailsMedRecord.text.toString() + r + "\n")

        for (x in args.medRecords)
            rec.add(x)

        update["medRecord"] = rec

        ref.updateChildren(update as Map<String, Any>)

        // TODO reach VetMainFragment
        /*val f = activity!!.supportFragmentManager.fragments[0].childFragmentManager.fragments[0]

        if (f is VetMainFragment)
            f.setMedRecord(rec)*/
    }
}
