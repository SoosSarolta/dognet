package hu.bme.aut.dognet.trainer

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
import hu.bme.aut.dognet.dialog_fragment.trainer.NewOrReviewTrainingDialogFragment
import hu.bme.aut.dognet.dialog_fragment.trainer.NewTrainingDialogFragment
import hu.bme.aut.dognet.trainer.adapter.TrainerAdapter
import hu.bme.aut.dognet.trainer.model.TrainerDbEntry
import hu.bme.aut.dognet.trainer.model.TrainingsDbEntry
import hu.bme.aut.dognet.util.Callback
import hu.bme.aut.dognet.util.TRAINER_FIREBASE_ENTRY
import kotlinx.android.synthetic.main.fragment_trainer_main.*

// TODO when coming back from TrainerDetailsFragment, don't show 'Start new or review' dialog
class TrainerMainFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var trainerAdapter: TrainerAdapter

    private lateinit var date: String
    private lateinit var group: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trainer_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setDrawerEnabled(false)

        db = Firebase.firestore

        val decisionDialogFragment = NewOrReviewTrainingDialogFragment()
        (activity as MainActivity).supportFragmentManager.let { decisionDialogFragment.show(it, "new_or_review_dialog") }
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
        (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "new_training_dialog") }
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
        val pets: MutableList<TrainerDbEntry> = ArrayList()

        val entry = hashMapOf(
            "date" to this.date,
            "group" to this.group
        )

        val trainingsEntry = TrainingsDbEntry.create()
        trainingsEntry.date = entry["date"].toString()
        trainingsEntry.group = entry["group"].toString()

        trainingsEntry.pets = pets

        db.collection(TRAINER_FIREBASE_ENTRY).document(this.date).set(entry)
            .addOnSuccessListener {
                Toast.makeText(activity, "Entry added to database!", Toast.LENGTH_LONG).show()
                trainerAdapter.addEntry(trainingsEntry)
                trainerAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Error!", Toast.LENGTH_LONG).show()
            }

        newTrainingCreated(trainingsEntry)
    }

    fun setData(date: String, group: String) {
        this.date = date
        this.group = group

        addDbEntry()
    }

    fun checkEntryAlreadyInDb(myDate: String, callback: Callback) {
        date = myDate

        val dateRef = db.collection(TRAINER_FIREBASE_ENTRY)
        val query = dateRef.whereEqualTo("date", myDate)
        query.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result!!.isEmpty) {
                    callback.onCallback()
                }
                else {
                    for (dsnap in it.result!!) {
                        val tempDate = dsnap.getString("date")
                        if (tempDate.equals(myDate))
                            Toast.makeText(activity, "Training already added!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun initTrainerEntryListener() {
        db.collection(TRAINER_FIREBASE_ENTRY).get()
            .addOnSuccessListener { result ->
                for (document in result)
                    trainerAdapter.addEntry(document.toObject())
            }
    }

    private fun trainerDbEntryClicked(item: TrainingsDbEntry) {
        findNavController().navigate(TrainerMainFragmentDirections.actionTrainerMainFragmentToTrainerDetailsFragment(item.date, item.group))
    }
}
