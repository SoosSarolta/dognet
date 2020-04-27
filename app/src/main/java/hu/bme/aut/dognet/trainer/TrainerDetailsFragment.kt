package hu.bme.aut.dognet.trainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.dialog_fragment.ChipReadDialogFragment
import hu.bme.aut.dognet.dialog_fragment.trainer.TrainerFormDialogFragment
import hu.bme.aut.dognet.trainer.adapter.DogsInTrainingAdapter
import hu.bme.aut.dognet.trainer.model.TrainerDbEntry
import hu.bme.aut.dognet.util.DB
import hu.bme.aut.dognet.util.TRAINER_FIREBASE_ENTRY
import kotlinx.android.synthetic.main.fragment_trainer_details.*
import kotlinx.android.synthetic.main.fragment_trainer_details.fab

// TODO replace deprecated fragment manager calls
class TrainerDetailsFragment : Fragment() {

    private val args: TrainerDetailsFragmentArgs by navArgs()

    lateinit var petsInTrainingAdapter: DogsInTrainingAdapter

    private var petsList: MutableList<String> = ArrayList()
    private var pets: MutableList<TrainerDbEntry> = ArrayList()

    private lateinit var chip: String
    private lateinit var petName: String
    private lateinit var breed: String
    private lateinit var ownerName: String
    private lateinit var phoneNum: String
    private lateinit var petGroup: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trainer_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvDetailsTrainingDate.text = args.itemDate.toString()
        tvDetailsTrainingGroup.text = args.trainingGroup.toString()

        petsInTrainingAdapter = DogsInTrainingAdapter(activity!!.applicationContext) { item: TrainerDbEntry -> trainerDbEntryClicked(item) }
        recyclerView.layoutManager = LinearLayoutManager(activity).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        recyclerView.adapter = petsInTrainingAdapter

        fab.setOnClickListener {
            val dialogFragment = ChipReadDialogFragment()
            fragmentManager?.let { dialogFragment.show(it, "dialog") }
        }

        initDogsInTrainingEntryListener()
    }

    fun checkEntryAlreadyInList(chipNum: String): Boolean {
        this.chip = chipNum
        return petsList.contains(chipNum)
    }

    fun openTrainerDataForm() {
        val dialogFragment = TrainerFormDialogFragment()
        fragmentManager?.let { dialogFragment.show(it, "data_dialog_trainer") }
    }

    fun setData(petName: String, breed: String, ownerName: String, phoneNum: String, petGroup: String) {
        this.petName = petName
        this.ownerName = ownerName
        this.phoneNum = phoneNum
        this.petGroup = petGroup

        if (breed == "")
            this.breed = "Unknown"
        else
            this.breed = breed

        refreshDbEntry()
    }

    private fun refreshDbEntry() {
        val myDate = args.itemDate.toString()
        val dateRef = DB.child(TRAINER_FIREBASE_ENTRY).child(myDate)

        val entry = TrainerDbEntry.create()
        entry.chipNum = this.chip
        entry.petName = this.petName
        entry.breed = this.breed
        entry.ownerName = this.ownerName
        entry.phoneNum = this.phoneNum
        entry.group = this.petGroup

        entry.trainings = ArrayList()

        val update: MutableMap<String, MutableList<TrainerDbEntry>> = HashMap()

        pets.add(entry)
        update["pets"] = pets
        dateRef.updateChildren(update as Map<String, Any>)

        // TODO reach TrainerMainFragment
    }

    private fun initDogsInTrainingEntryListener() {
        FirebaseDatabase.getInstance().getReference(TRAINER_FIREBASE_ENTRY)
            .child(args.itemDate.toString())
            .child("pets")
            .addChildEventListener(object: ChildEventListener {
                override fun onChildAdded(dataSnaphot: DataSnapshot, s: String?) {
                    val newEntry = dataSnaphot.getValue<TrainerDbEntry>(TrainerDbEntry::class.java)
                    petsInTrainingAdapter.addEntry(newEntry)
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) { }

                override fun onChildRemoved(p0: DataSnapshot) { }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) { }

                override fun onCancelled(p0: DatabaseError) { }
            })
    }

    private fun trainerDbEntryClicked(item: TrainerDbEntry) {
        findNavController().navigate(TrainerDetailsFragmentDirections.actionTrainerDetailsFragmentToTrainerDetailsDetailsFragment(item.chipNum, item.petName, item.breed, item.ownerName, item.phoneNum, item.group, item.trainings.toTypedArray()))
    }
}
