package hu.bme.aut.dognet.vet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.dialog_fragment.ChipReadDialogFragment
import hu.bme.aut.dognet.dialog_fragment.VetFormDialogFragment
import hu.bme.aut.dognet.util.VET_FIREBASE_ENTRY
import hu.bme.aut.dognet.vet.adapter.VetAdapter
import hu.bme.aut.dognet.vet.model.VetDbEntry
import kotlinx.android.synthetic.main.fragment_vet_main.*
import java.util.*


class VetMainFragment : Fragment() {
    lateinit var db: DatabaseReference
    lateinit var vetAdapter: VetAdapter

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

        db = FirebaseDatabase.getInstance().reference

        vetAdapter = VetAdapter(activity!!.applicationContext) { item: VetDbEntry -> vetDbEntryClicked(item) }
        recyclerView.layoutManager = LinearLayoutManager(activity).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        recyclerView.adapter = vetAdapter

        fab.setOnClickListener {
            val dialogFragment = ChipReadDialogFragment()
            fragmentManager?.let { dialogFragment.show(it, "dialog") }
        }

        initVetEntryListener()
    }

    private fun addDbEntry() {
        db.child(VET_FIREBASE_ENTRY).push().key ?: return

        val entry = VetDbEntry.create()

        entry.chipNum = this.chip
        entry.petName = this.petName
        entry.breed = this.breed
        entry.sex = this.petSex
        entry.dob = this.dob
        entry.ownerName = this.ownerName
        entry.ownerAddress = this.address
        entry.phoneNum = this.phone

        val newEntry = db.child(VET_FIREBASE_ENTRY).push()
        newEntry.setValue(entry)
        Toast.makeText(this.activity!!, "Entry added to database!", Toast.LENGTH_LONG).show()
    }

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

    fun checkEntryAlreadyInDb(chipNum: String): Boolean {
        chip = chipNum
        var flag = 0

        val chipNumRef = db.child(VET_FIREBASE_ENTRY).child(chipNum)
        chipNumRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Firebase", "Database Error!")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                    flag = 1
            }
        })

        return flag == 1
    }

    fun openVetDataForm() {
        val dialogFragment = VetFormDialogFragment()
        fragmentManager?.let { dialogFragment.show(it, "data_dialog_vet") }
    }

    private fun initVetEntryListener() {
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
        findNavController().navigate(VetMainFragmentDirections.actionVetMainFragmentToVetDetailsFragment(item.chipNum))
    }
}
