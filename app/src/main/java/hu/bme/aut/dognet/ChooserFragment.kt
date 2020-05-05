package hu.bme.aut.dognet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_chooser.*

class ChooserFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chooser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).title = "DogNet"

        (activity as MainActivity).setDrawerEnabled(true)

        vetButton.setOnClickListener {
            view.findNavController().navigate(ChooserFragmentDirections.actionChooserFragmentToVetMainFragment())
        }

        trainerButton.setOnClickListener {
            view.findNavController().navigate(ChooserFragmentDirections.actionChooserFragmentToTrainerMainFragment())
        }

        lostDogButton.setOnClickListener {
            view.findNavController().navigate(ChooserFragmentDirections.actionChooserFragmentToLostMainFragment())
        }

        foundDogButton.setOnClickListener {
            view.findNavController().navigate(ChooserFragmentDirections.actionChooserFragmentToFoundMainFragment())

        }
    }
}
