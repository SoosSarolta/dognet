package hu.bme.aut.dognet.vet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import hu.bme.aut.dognet.MainActivity
import hu.bme.aut.dognet.util.Callback
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.dialog_fragment.ChipReadDialogFragment
import hu.bme.aut.dognet.dialog_fragment.vet.VetFormDialogFragment
import hu.bme.aut.dognet.util.DB
import hu.bme.aut.dognet.util.VET_FIREBASE_ENTRY
import hu.bme.aut.dognet.vet.adapter.VetAdapter
import hu.bme.aut.dognet.vet.model.VetDbEntry
import kotlinx.android.synthetic.main.fragment_vet_main.*

// TODO migrate to firestore
// TODO replace deprecated fragment manager calls
class VetMainFragment : Fragment() {
    //lateinit var db: DatabaseReference
    //lateinit var db: FirebaseFirestore
    lateinit var vetAdapter: VetAdapter

    private lateinit var entry: VetDbEntry

    lateinit var chip: String
    lateinit var petName: String
    lateinit var breed: String
    lateinit var petSex: String
    lateinit var dob: String
    lateinit var ownerName: String
    lateinit var address: String
    lateinit var phone: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vet_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //db = FirebaseDatabase.getInstance().reference
        //db = Firebase.firestore

        vetAdapter = VetAdapter(activity!!.applicationContext) { item: VetDbEntry -> vetDbEntryClicked(item) }
        recyclerView.layoutManager = LinearLayoutManager(activity).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        recyclerView.adapter = vetAdapter

        (activity as MainActivity).setDrawerEnabled(false)

        fab.setOnClickListener {
            val dialogFragment = ChipReadDialogFragment()
            fragmentManager?.let { dialogFragment.show(it, "dialog") }
        }

        initVetEntryListener()
    }

    private fun addDbEntry() {
        /*val entry = hashMapOf(
            "chipNum" to this.chip,
            "petName" to this.petName,
            "breed" to this.breed,
            "sex" to this.petSex,
            "dob" to this.dob,
            "ownerName" to this.ownerName,
            "ownerAddress" to this.address,
            "phoneNum" to this.phone
        )

        val vetEntry = VetDbEntry.create()
        vetEntry.chipNum = entry["chipNum"]
        vetEntry.petName = entry["petName"]
        vetEntry.breed = entry["breed"]
        vetEntry.sex = entry["sex"]
        vetEntry.dob = entry["dob"]
        vetEntry.ownerName = entry["ownerName"]
        vetEntry.ownerAddress = entry["ownerAddress"]
        vetEntry.phoneNum = entry["phoneNum"]

        db.collection(VET_FIREBASE_ENTRY).add(entry)
            .addOnSuccessListener { Toast.makeText(this.activity!!, "Entry added to database!", Toast.LENGTH_LONG).show() }
            .addOnFailureListener { Toast.makeText(this.activity!!, "Error!", Toast.LENGTH_LONG).show() }*/

        DB.child(VET_FIREBASE_ENTRY).push().key ?: return

        entry = VetDbEntry.create()

        entry.chipNum = this.chip
        entry.petName = this.petName
        entry.breed = this.breed
        entry.sex = this.petSex
        entry.dob = this.dob
        entry.ownerName = this.ownerName
        entry.ownerAddress = this.address
        entry.phoneNum = this.phone

        entry.vaccinations = HashMap()
        entry.medRecord = ArrayList()

        val pets: MutableMap<String, VetDbEntry> = HashMap()
        pets[this.chip] = entry

        val ref = DB.child(VET_FIREBASE_ENTRY)
        ref.updateChildren(pets as Map<String, Any>)
        Toast.makeText(this.activity!!, "Entry added to database!", Toast.LENGTH_LONG).show()
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

        /*val chipNumRef = db.collection(VET_FIREBASE_ENTRY)
        val query = chipNumRef.whereEqualTo("chipNum", chipNum)
        query.get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (dsnap in it.result!!) run {
                    val tempChip = dsnap.getString("chipNum")
                    if (tempChip.equals(chipNum))
                        flag = 1
                }
            }
        }*/

        val chipNumRef = DB.child(VET_FIREBASE_ENTRY).child(chipNum)
        chipNumRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Firebase", "Database Error!")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists())
                    callback.onCallback()
                else
                    Toast.makeText(activity, "Pet already added!", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun openVetDataForm() {
        val dialogFragment = VetFormDialogFragment()
        fragmentManager?.let { dialogFragment.show(it, "data_dialog_vet") }
    }

    private fun initVetEntryListener() {
        /*db.collection(VET_FIREBASE_ENTRY).get()
            .addOnSuccessListener { result ->
                for (document in result)
                    vetAdapter.addEntry(document.toObject())
            }*/

        FirebaseDatabase.getInstance().getReference(VET_FIREBASE_ENTRY)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val newEntry = dataSnapshot.getValue<VetDbEntry>(VetDbEntry::class.java)
                    vetAdapter.addEntry(newEntry)
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

                override fun onChildRemoved(p0: DataSnapshot) {}

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

                override fun onCancelled(p0: DatabaseError) {}
            })
    }

    private fun vetDbEntryClicked(item: VetDbEntry) {
        findNavController().navigate(VetMainFragmentDirections.actionVetMainFragmentToVetDetailsFragment(item.chipNum, item.petName, item.breed, item.sex, item.ownerName, item.ownerAddress, item.phoneNum, item.dob, item.vaccinations.keys.toTypedArray(), item.vaccinations.values.toTypedArray(), item.medRecord.toTypedArray()))
    }
}
