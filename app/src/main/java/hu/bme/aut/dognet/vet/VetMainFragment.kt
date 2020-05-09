package hu.bme.aut.dognet.vet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hu.bme.aut.dognet.MainActivity
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.dialog_fragment.ChipReadDialogFragment
import hu.bme.aut.dognet.dialog_fragment.vet.VetFormDialogFragment
import hu.bme.aut.dognet.util.Callback
import hu.bme.aut.dognet.util.VET_FIREBASE_ENTRY
import hu.bme.aut.dognet.vet.adapter.VetAdapter
import hu.bme.aut.dognet.vet.model.VetDbEntry
import kotlinx.android.synthetic.main.fragment_vet_main.*

class VetMainFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var vetAdapter: VetAdapter

    private lateinit var chip: String
    private lateinit var petName: String
    private lateinit var breed: String
    private lateinit var petSex: String
    private lateinit var dob: String
    private lateinit var ownerName: String
    private lateinit var address: String
    private lateinit var phone: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vet_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = Firebase.firestore

        vetAdapter = VetAdapter(activity!!.applicationContext) { item: VetDbEntry -> vetDbEntryClicked(item) }
        recyclerView.layoutManager = LinearLayoutManager(activity).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        recyclerView.adapter = vetAdapter

        (activity as MainActivity).setDrawerEnabled(false)

        fab.setOnClickListener {
            val dialogFragment = ChipReadDialogFragment()
            (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "dialog") }
        }

        initVetEntryListener()
    }

    private fun addDbEntry() {
        val vacc: MutableList<String> = ArrayList()
        val medRec: MutableList<String> = ArrayList()

        val entry = hashMapOf(
            "chipNum" to this.chip,
            "petName" to this.petName,
            "breed" to this.breed,
            "sex" to this.petSex,
            "dob" to this.dob,
            "ownerName" to this.ownerName,
            "ownerAddress" to this.address,
            "phoneNum" to this.phone,
            "vaccinations" to vacc,
            "medRecord" to medRec
        )

        val vetEntry = VetDbEntry.create()
        vetEntry.chipNum = entry["chipNum"].toString()
        vetEntry.petName = entry["petName"].toString()
        vetEntry.breed = entry["breed"].toString()
        vetEntry.sex = entry["sex"].toString()
        vetEntry.dob = entry["dob"].toString()
        vetEntry.ownerName = entry["ownerName"].toString()
        vetEntry.ownerAddress = entry["ownerAddress"].toString()
        vetEntry.phoneNum = entry["phoneNum"].toString()

        vetEntry.vaccinations = vacc
        vetEntry.medRecord = medRec

        db.collection(VET_FIREBASE_ENTRY).document(this.chip).set(entry)
            .addOnSuccessListener {
                Toast.makeText(this.activity!!, "Entry added to database!", Toast.LENGTH_LONG).show()
                vetAdapter.addEntry(vetEntry)
                vetAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this.activity!!, "Error!", Toast.LENGTH_LONG).show()
            }
    }

    /*fun setVaccinations(vacc: MutableMap<String, String>) {
        entry.vaccinations = vacc
    }

    fun setMedRecord(rec: MutableList<String>) {
        entry.medRecord = rec
    }*/

    fun setData(petName: String, breed: String, sex: String, dob: String, ownerName: String, address: String, phone: String) {
        this.petName = petName
        this.petSex = sex
        this.ownerName = ownerName
        this.address = address
        this.phone = phone

        if (breed == "")
            this.breed = "Unknown"
        else
            this.breed = breed

        if (dob == "")
            this.dob = "Unknown"
        else
            this.dob = dob

        addDbEntry()
    }

    fun checkEntryAlreadyInDb(chipNum: String, callback: Callback) {
        chip = chipNum

        val chipNumRef = db.collection(VET_FIREBASE_ENTRY)
        val query = chipNumRef.whereEqualTo("chipNum", chipNum)
        query.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result!!.isEmpty) {
                    callback.onCallback()
                }
                else {
                    for (dsnap in it.result!!) {
                        val tempChip = dsnap.getString("chipNum")
                        if (tempChip.equals(chipNum))
                            Toast.makeText(activity, "Pet already added!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun openVetDataForm() {
        val dialogFragment = VetFormDialogFragment()
        (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "data_dialog_vet") }
    }

    private fun initVetEntryListener() {
        db.collection(VET_FIREBASE_ENTRY).get()
            .addOnSuccessListener { result ->
                for (document in result)
                        vetAdapter.addEntry(document.toObject())
            }
    }

    private fun vetDbEntryClicked(item: VetDbEntry) {
        findNavController().navigate(VetMainFragmentDirections.actionVetMainFragmentToVetDetailsFragment(item.chipNum, item.petName, item.breed, item.sex, item.ownerName, item.ownerAddress, item.phoneNum, item.dob, item.vaccinations.toTypedArray(), item.medRecord.toTypedArray()))
    }
}
