package hu.bme.aut.dognet.vet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.dialog_fragment.vet.EditMedRecordDialogFragment
import hu.bme.aut.dognet.dialog_fragment.vet.EditVaccinationsDialogFragment
import hu.bme.aut.dognet.util.DB
import hu.bme.aut.dognet.util.VET_FIREBASE_ENTRY
import kotlinx.android.synthetic.main.fragment_vet_details.*

// TODO replace deprecated fragment manager calls
class VetDetailsFragment : Fragment() {

    private val args: VetDetailsFragmentArgs by navArgs()

    private var vaccinations: MutableList<String> = ArrayList()
    private var records: MutableList<String> = ArrayList()

    private lateinit var adapterVacc: ArrayAdapter<String>
    private lateinit var adapterMed: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vet_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        for (x in args.vaccNames.indices)
            vaccinations.add(args.vaccNames[x] + " - " + args.vaccDates[x])
            //etDetailsVaccinations.setText(args.vaccNames[x] + " - " + args.vaccDates[x] + "\n")

        for (x in args.medRecords)
            records.add(x)
            //etDetailsMedRecord.setText(x + "\n")

        adapterVacc = ArrayAdapter<String>(activity!!, android.R.layout.simple_list_item_1, vaccinations)
        vaccListView.adapter = adapterVacc

        adapterMed = ArrayAdapter<String>(activity!!, android.R.layout.simple_list_item_1, records)
        medRecListView.adapter = adapterMed

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

        vacc.forEach { vaccinations.add(it.key + " - " + it.value) }
        adapterVacc.notifyDataSetChanged()

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
            records.add(r)
        adapterMed.notifyDataSetChanged()

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
