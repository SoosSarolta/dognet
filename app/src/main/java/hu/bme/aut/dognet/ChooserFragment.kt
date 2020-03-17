package hu.bme.aut.dognet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController


class ChooserFragment : Fragment() {

    private lateinit var vetButton: Button
    private lateinit var trainerButton: Button
    private lateinit var lostButton: Button
    private lateinit var foundButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chooser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).title = "DogNet"

        vetButton = view.findViewById(R.id.vetButton)
        trainerButton = view.findViewById(R.id.trainerButton)
        lostButton = view.findViewById(R.id.lostDogButton)
        foundButton = view.findViewById(R.id.foundDogButton)

        vetButton.setOnClickListener {
            view.findNavController().navigate(ChooserFragmentDirections.actionChooserFragmentToVetMainFragment())
        }

        trainerButton.setOnClickListener {
            view.findNavController().navigate(ChooserFragmentDirections.actionChooserFragmentToTrainerMainFragment())
        }

        lostButton.setOnClickListener {
            view.findNavController().navigate(ChooserFragmentDirections.actionChooserFragmentToLostMainFragment())
        }

/*        foundButton.setOnClickListener {
            view.findNavController().navigate(ChooserFragmentDirections.actionChooserFragmentToLostMainFragment())

        }*/
    }
}
