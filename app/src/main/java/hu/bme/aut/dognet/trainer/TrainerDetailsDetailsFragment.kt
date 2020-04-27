package hu.bme.aut.dognet.trainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import hu.bme.aut.dognet.R
import kotlinx.android.synthetic.main.fragment_trainer_details_details.*


class TrainerDetailsDetailsFragment : Fragment() {

    private val args: TrainerDetailsDetailsFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trainer_details_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvDetailsDetailsChip.text = args.chip.toString()
        tvDetailsDetailsPetName.text = args.petName.toString()
        tvDetailsDetailsBreed.text = args.breed.toString()
        tvDetailsDetailsOwnerName.text = args.ownerName.toString()
        tvDetailsDetailsPhone.text = args.phone.toString()

        tvDetailsDetailsGroup.text = args.group.toString()

        // TODO how to add training dates
        for (x in args.trainingDates.indices)
            etDetailsDetailsTrainings.setText(args.trainingDates[x] + "\n")
    }
}
