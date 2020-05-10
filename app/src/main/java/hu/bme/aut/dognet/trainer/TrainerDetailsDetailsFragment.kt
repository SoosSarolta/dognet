package hu.bme.aut.dognet.trainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import hu.bme.aut.dognet.R
import kotlinx.android.synthetic.main.fragment_trainer_details_details.*

class TrainerDetailsDetailsFragment : Fragment() {

    private val args: TrainerDetailsDetailsFragmentArgs by navArgs()

    private val trainings: MutableList<String> = ArrayList()

    private lateinit var adapterTrainings: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trainer_details_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvDetailsDetailsChip.text = args.chip.toString()
        tvDetailsDetailsPetName.text = args.petName.toString()

        if (args.breed.toString() != "null")
            tvDetailsDetailsBreed.text = args.breed.toString()
        else
            tvDetailsDetailsBreed.text = getString(R.string.unkown)

        tvDetailsDetailsOwnerName.text = args.ownerName.toString()
        tvDetailsDetailsPhone.text = args.phone.toString()

        tvDetailsDetailsGroup.text = args.group.toString()

        for (x in args.trainingDates)
           trainings.add(x)



        adapterTrainings = ArrayAdapter(activity!!, android.R.layout.simple_list_item_1, trainings)
        trainingsListView.adapter = adapterTrainings
    }
}
