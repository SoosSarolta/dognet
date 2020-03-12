package hu.bme.aut.dognet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController


class ChooserFragment : Fragment() {

    private lateinit var vetButton: ImageButton
    private lateinit var trainerButton: ImageButton
    private lateinit var lostButton: ImageButton
    private lateinit var foundButton: ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chooser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vetButton = view!!.findViewById(R.id.vetImageButton)
        trainerButton = view!!.findViewById(R.id.trainerImageButton)
        lostButton = view!!.findViewById(R.id.lostDogImageButton)
        foundButton = view!!.findViewById(R.id.foundDogImageButton)

        vetButton.setOnClickListener {
            val action = ChooserFragmentDirections.actionChooserFragmentToVetMainFragment()
            view.findNavController().navigate(action)
        }

        trainerButton.setOnClickListener {
            val action = ChooserFragmentDirections.actionChooserFragmentToTrainerMainFragment()
            view.findNavController().navigate(action)
        }

        lostButton.setOnClickListener {
            val action = ChooserFragmentDirections.actionChooserFragmentToLostMainFragment()
            view.findNavController().navigate(action)
        }
    }
}
