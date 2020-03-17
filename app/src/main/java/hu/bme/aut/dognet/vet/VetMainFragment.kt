package hu.bme.aut.dognet.vet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.dialog_fragment.ChipReadDialogFragment


class VetMainFragment : Fragment() {

    private lateinit var floatingActionButton: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vet_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        floatingActionButton = view.findViewById(R.id.fab)

        floatingActionButton.setOnClickListener {
            val dialogFragment = ChipReadDialogFragment()
            fragmentManager?.let { dialogFragment.show(it, "dialog") }
        }
    }
}
