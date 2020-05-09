package hu.bme.aut.dognet.trainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hu.bme.aut.dognet.MainActivity
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.dialog_fragment.ChipReadDialogFragment
import hu.bme.aut.dognet.dialog_fragment.trainer.TrainerFormDialogFragment
import hu.bme.aut.dognet.trainer.adapter.DogsInTrainingAdapter
import hu.bme.aut.dognet.trainer.model.TrainerDbEntry
import hu.bme.aut.dognet.util.TRAINER_FIREBASE_ENTRY
import kotlinx.android.synthetic.main.fragment_trainer_details.*

class TrainerDetailsFragment : Fragment() {

    private val args: TrainerDetailsFragmentArgs by navArgs()

    private lateinit var db: FirebaseFirestore
    private lateinit var petsInTrainingAdapter: DogsInTrainingAdapter

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

        db = Firebase.firestore

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
            (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "dialog") }
        }

        initDogsInTrainingEntryListener()
    }

    fun getGroup(): String {
        return args.trainingGroup.toString()
    }

    fun checkEntryAlreadyInList(chipNum: String): Boolean {
        this.chip = chipNum
        return petsList.contains(chipNum)
    }

    fun openTrainerDataForm() {
        val dialogFragment = TrainerFormDialogFragment()
        (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "data_dialog_trainer") }
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

        val entry = TrainerDbEntry.create()
        entry.chipNum = this.chip
        entry.petName = this.petName
        entry.breed = this.breed
        entry.ownerName = this.ownerName
        entry.phoneNum = this.phoneNum
        entry.group = this.petGroup

        entry.trainings = ArrayList()

        pets.add(entry)

        val ref = db.collection(TRAINER_FIREBASE_ENTRY).document(myDate).collection("pets").document((pets.size).toString())
        ref.set(entry)

        petsInTrainingAdapter.addEntry(entry)
        petsInTrainingAdapter.notifyDataSetChanged()

        // TODO reach TrainerMainFragment
    }

    private fun initDogsInTrainingEntryListener() {
        db.collection(TRAINER_FIREBASE_ENTRY).document(args.itemDate.toString()).collection("pets").get()
            .addOnSuccessListener { result ->
                for (document in result)
                    petsInTrainingAdapter.addEntry(document.toObject())
            }
    }

    private fun trainerDbEntryClicked(item: TrainerDbEntry) {
        findNavController().navigate(TrainerDetailsFragmentDirections.actionTrainerDetailsFragmentToTrainerDetailsDetailsFragment(item.chipNum, item.petName, item.breed, item.ownerName, item.phoneNum, item.group, item.trainings.toTypedArray()))
    }
}
