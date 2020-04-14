package hu.bme.aut.dognet.trainer

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
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.dialog_fragment.ChipReadDialogFragment
import hu.bme.aut.dognet.dialog_fragment.TrainerFormDialogFragment
import hu.bme.aut.dognet.trainer.adapter.TrainerAdapter
import hu.bme.aut.dognet.trainer.model.TrainerDbEntry
import hu.bme.aut.dognet.util.TRAINER_FIREBASE_ENTRY
import kotlinx.android.synthetic.main.fragment_trainer_main.*


class TrainerMainFragment : Fragment() {
    lateinit var db: DatabaseReference
    lateinit var trainerAdapter: TrainerAdapter

    lateinit var chip: String
    lateinit var petName: String
    lateinit var breed: String
    lateinit var ownerName: String
    lateinit var phone: String
    lateinit var group: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trainer_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseDatabase.getInstance().reference

        trainerAdapter = TrainerAdapter(activity!!.applicationContext) { item: TrainerDbEntry -> trainerDbEntryClicked(item) }
        recyclerView.layoutManager = LinearLayoutManager(activity).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        recyclerView.adapter = trainerAdapter

        fab.setOnClickListener {
            val dialogFragment = ChipReadDialogFragment()
            fragmentManager?.let { dialogFragment.show(it, "dialog") }
        }

        initTrainerEntryListener()
    }

    private fun addDbEntry() {
        db.child(TRAINER_FIREBASE_ENTRY).push().key ?: return

        val entry = TrainerDbEntry.create()

        entry.chipNum = this.chip
        entry.petName = this.petName
        entry.breed = this.breed
        entry.ownerName = this.ownerName
        entry.phoneNum = this.phone
        entry.group = this.group

        val newEntry = db.child(TRAINER_FIREBASE_ENTRY).push()
        newEntry.setValue(entry)
        Toast.makeText(this.activity!!, "Entry added to database!", Toast.LENGTH_LONG).show()
    }

    fun setData(petName: String, breed: String, ownerName: String, phone: String, group: String) {
        this.petName = petName
        this.ownerName = ownerName
        this.phone = phone
        this.group = group

        if (breed == "")
            this.breed = "Unknown"
        else
            this.breed = breed

        addDbEntry()
    }

    fun checkEntryAlreadyInDb(chipNum: String): Boolean {
        chip = chipNum
        var flag = 0

        val chipNumRef = db.child(TRAINER_FIREBASE_ENTRY).child(chipNum)
        chipNumRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Firebase", "Database error!")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                    flag = 1
            }
        })

        return flag == 1
    }

    fun openTrainerDataForm() {
        val dialogFragment = TrainerFormDialogFragment()
        fragmentManager?.let { dialogFragment.show(it, "data_dialog_trainer") }
    }

    private fun initTrainerEntryListener() {
        FirebaseDatabase.getInstance().getReference(TRAINER_FIREBASE_ENTRY)
            .addChildEventListener(object: ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val newEntry = dataSnapshot.getValue<TrainerDbEntry>(TrainerDbEntry::class.java)
                    trainerAdapter.addEntry(newEntry)
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) { }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) { }

                override fun onChildRemoved(p0: DataSnapshot) { }

                override fun onCancelled(p0: DatabaseError) { }
            })
    }

    private fun trainerDbEntryClicked(item: TrainerDbEntry) {
        findNavController().navigate(TrainerMainFragmentDirections.actionTrainerMainFragmentToTrainerDetailsFragment(item.chipNum))
    }
}
