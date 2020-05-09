package hu.bme.aut.dognet.lost_n_found

import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import hu.bme.aut.dognet.R
import kotlinx.android.synthetic.main.fragment_found_details.*

class FoundDetailsFragment : Fragment() {

    private val args: FoundDetailsFragmentArgs by navArgs()

    private lateinit var googleMap: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_found_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.itemChipNum.toString() != "null")
            tvDetailsChip.text = args.itemChipNum.toString()
        else
            tvDetailsChip.text = " - "

        if (args.breed.toString() != "null")
            tvDetailsBreed.text = args.breed.toString()
        else
            tvDetailsBreed.text = getString(R.string.unkown)

        if (args.sex.toString() != "null")
            tvDetailsSex.text = args.sex.toString()
        else
            tvDetailsSex.text = getString(R.string.unkown)

        if (args.extraInfo.toString() != "null")
            tvDetailsExtraInfo.text = args.extraInfo.toString()
        else
            tvDetailsExtraInfo.text = " - "

        if (args.photo.toString() != "null") {
            val imageBytes = android.util.Base64.decode(args.photo.toString(), android.util.Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            photoOfPetImageView.setImageBitmap(decodedImage)
        }
        else {
            photoOfPetImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.placeholder_image, null))
        }

        map.onCreate(savedInstanceState)
        map.onResume()

        MapsInitializer.initialize(activity!!.applicationContext)

        map.getMapAsync {
            googleMap = it

            val geocoder = Geocoder(activity)
            val address: MutableList<Address>? = geocoder.getFromLocationName(args.foundAt.toString(), 3)

            if (address != null && address.size > 0) {
                tvNoLocationSpecified.text = ""

                val loc = address[0]
                val latitude = loc.latitude
                val longitude = loc.longitude

                val markerPoint = LatLng(latitude, longitude)
                googleMap.addMarker(MarkerOptions().position(markerPoint).title("Found At"))

                val cameraPosition = CameraPosition.Builder().target(markerPoint).zoom(10F).build()
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
            else {
                tvNoLocationSpecified.text = getString(R.string.no_location)
            }
        }
    }
}
