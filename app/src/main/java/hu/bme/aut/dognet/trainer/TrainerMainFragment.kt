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
import hu.bme.aut.dognet.dialog_fragment.trainer.NewOrReviewTrainingDialogFragment
import hu.bme.aut.dognet.dialog_fragment.trainer.NewTrainingDialogFragment
import hu.bme.aut.dognet.trainer.adapter.TrainerAdapter
import hu.bme.aut.dognet.trainer.model.TrainingsDbEntry
import hu.bme.aut.dognet.util.Callback
import hu.bme.aut.dognet.util.DB
import hu.bme.aut.dognet.util.TRAINER_FIREBASE_ENTRY
import kotlinx.android.synthetic.main.fragment_trainer_main.*

// TODO migrate to firestore
// TODO replace deprecated fragment manager calls
// TODO when coming back from TrainerDetailsFragment, don't show 'Start new or review' dialog
class TrainerMainFragment : Fragment() {
    lateinit var trainerAdapter: TrainerAdapter

    private lateinit var entry: TrainingsDbEntry

    lateinit var date: String
    lateinit var group: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trainer_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val decisionDialogFragment = NewOrReviewTrainingDialogFragment()
        fragmentManager?.let { decisionDialogFragment.show(it, "new_or_review_dialog") }

    }

    fun reviewTrainingBtnPressed() {
        trainerAdapter = TrainerAdapter(activity!!.applicationContext) { item: TrainingsDbEntry -> trainerDbEntryClicked(item) }
        recyclerView.layoutManager = LinearLayoutManager(activity).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        recyclerView.adapter = trainerAdapter

        fab.setOnClickListener {
            startNewTrainingBtnPressed()
        }

        initTrainerEntryListener()
    }

    fun startNewTrainingBtnPressed() {
        val dialogFragment = NewTrainingDialogFragment()
        fragmentManager?.let { dialogFragment.show(it, "new_training_dialog") }
    }

    private fun newTrainingCreated(myItem: TrainingsDbEntry) {
        trainerAdapter = TrainerAdapter(activity!!.applicationContext) { item: TrainingsDbEntry -> trainerDbEntryClicked(item) }
        recyclerView.layoutManager = LinearLayoutManager(activity).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        recyclerView.adapter = trainerAdapter

        initTrainerEntryListener()

        trainerDbEntryClicked(myItem)
    }

    private fun addDbEntry() {
        DB.child(TRAINER_FIREBASE_ENTRY).push().key ?: return

        entry = TrainingsDbEntry.create()

        entry.date = this.date
        entry.group = this.group

        entry.pets = ArrayList()

        val trainings: MutableMap<String, TrainingsDbEntry> = HashMap()
        trainings[this.date] = entry

        val ref = DB.child(TRAINER_FIREBASE_ENTRY)
        ref.updateChildren(trainings as Map<String, Any>)
        Toast.makeText(this.activity!!, "Entry added to database!", Toast.LENGTH_LONG).show()

        newTrainingCreated(entry)
    }

    fun setData(date: String, group: String) {
        this.date = date
        this.group = group

        addDbEntry()
    }

    fun checkEntryAlreadyInDb(myDate: String, callback: Callback) {
        date = myDate

        val dateRef = DB.child(TRAINER_FIREBASE_ENTRY).child(myDate)
        dateRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Firebase", "Database error!")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists())
                    callback.onCallback()
            }
        })
    }

    private fun initTrainerEntryListener() {
        FirebaseDatabase.getInstance().getReference(TRAINER_FIREBASE_ENTRY)
            .addChildEventListener(object: ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val newEntry = dataSnapshot.getValue<TrainingsDbEntry>(TrainingsDbEntry::class.java)
                    trainerAdapter.addEntry(newEntry)
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) { }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) { }

                override fun onChildRemoved(p0: DataSnapshot) { }

                override fun onCancelled(p0: DatabaseError) { }
            })
    }

    private fun trainerDbEntryClicked(item: TrainingsDbEntry) {
        findNavController().navigate(TrainerMainFragmentDirections.actionTrainerMainFragmentToTrainerDetailsFragment(item.date, item.group))
    }
}
