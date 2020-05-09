package hu.bme.aut.dognet.lost_n_found

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hu.bme.aut.dognet.MainActivity
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.dialog_fragment.ChipReadDialogFragment
import hu.bme.aut.dognet.dialog_fragment.lost_n_found.AddImageDialogFragment
import hu.bme.aut.dognet.dialog_fragment.lost_n_found.FoundPetDataFormDialogFragment
import hu.bme.aut.dognet.lost_n_found.adapter.FoundAdapter
import hu.bme.aut.dognet.lost_n_found.model.FoundDbEntry
import hu.bme.aut.dognet.util.FOUND_FIREBASE_ENTRY
import hu.bme.aut.dognet.util.REQUEST_CODE_CAMERA
import hu.bme.aut.dognet.util.REQUEST_CODE_STORAGE
import kotlinx.android.synthetic.main.fragment_found_main.*
import java.io.ByteArrayOutputStream

class FoundMainFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var foundAdapter: FoundAdapter

    private lateinit var chip: String
    private lateinit var breed: String
    private lateinit var sex: String
    private lateinit var foundAt: String
    private lateinit var extraInfo: String

    private var photo: Bitmap? = null
    private var photoString: String? = null

    private var imageFromGallery = false
    private var noChip = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_found_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = Firebase.firestore

        foundAdapter = FoundAdapter(activity!!.applicationContext) { item: FoundDbEntry -> foundDbEntryClicked(item) }
        recyclerView.layoutManager = LinearLayoutManager(activity).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        recyclerView.adapter = foundAdapter

        (activity as MainActivity).setDrawerEnabled(false)

        fab.setOnClickListener {
            val dialogFragment = ChipReadDialogFragment()
            (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "dialog") }
        }

        initFoundEntryListener()
    }

    private fun addDbEntry() {
        val baos = ByteArrayOutputStream()
        val imageEncoded: String
        if (this.photo != null) {
            if (imageFromGallery)
                this.photo!!.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            else
                this.photo!!.compress(Bitmap.CompressFormat.PNG, 100, baos)

            imageEncoded = android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.DEFAULT)
            this.photoString = imageEncoded
        }
        else
            this.photoString = null

        val entry = hashMapOf(
            "chipNum" to this.chip,
            "breed" to this.breed,
            "sex" to this.sex,
            "foundAt" to this.foundAt,
            "additionalInfo" to this.extraInfo,
            "photo" to this.photoString
        )

        val foundEntry = FoundDbEntry.create()
        foundEntry.chipNum = entry["chipNum"].toString()
        foundEntry.breed = entry["breed"].toString()
        foundEntry.sex = entry["sex"].toString()
        foundEntry.foundAt = entry["foundAt"].toString()
        foundEntry.additionalInfo = entry["additionalInfo"].toString()
        foundEntry.photo = entry["photo"].toString()

        if (!noChip) {
            db.collection(FOUND_FIREBASE_ENTRY).document(this.chip).set(entry)
                .addOnSuccessListener {
                    Toast.makeText(activity, "Entry added to database!", Toast.LENGTH_LONG).show()
                    foundAdapter.addEntry(foundEntry)
                    foundAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Error!", Toast.LENGTH_LONG).show()
                }
        }
        else {
            // TODO - no chip in pet
        }
    }

    fun setData(breed: String, sex: String, foundAt: String, extra: String) {
        if (breed == "")
           this.breed = "Unknown"
        else
            this.breed = breed

        if (sex == "")
            this.sex = "Unknown"
        else
            this.sex = sex

        if (foundAt == "")
            this.foundAt = ""
        else
            this.foundAt = foundAt

        if (extra == "")
            this.extraInfo = "-"
        else
            this.extraInfo = extra

        openAddImageDialogFragment()
    }

    fun chipRead(chip: String) {
        this.chip = chip
        openFoundDataForm()
    }

    // TODO - no chip in pet
    fun noChipFound() {
        noChip = true
    }

    private fun openFoundDataForm() {
        val dialogFragment = FoundPetDataFormDialogFragment()
        (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "found_dialog") }
    }

    private fun openAddImageDialogFragment() {
        val dialogFragment = AddImageDialogFragment()
        (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "photo_dialog") }
    }

    fun makePhotoBtnClicked() {
        imageFromGallery = false

        if (!activity!!.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Toast.makeText(this.activity!!, "This device does not have a camera!", Toast.LENGTH_LONG).show()
            return
        }

        if (askForPermissions(Manifest.permission.CAMERA, REQUEST_CODE_CAMERA)) {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(this.activity!!.packageManager)?.also {
                    startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)
                }
            }
        }
    }

    fun pickPhotoBtnClicked() {
        imageFromGallery = true

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), REQUEST_CODE_STORAGE)
    }

    fun withoutPhotoBtnClicked() {
        this.photo = null
        addDbEntry()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            this.photo = imageBitmap

            addDbEntry()
        }

        if (requestCode == REQUEST_CODE_STORAGE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data

            uri?.let {
                if (Build.VERSION.SDK_INT < 28) {
                    val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri)
                    this.photo = bitmap
                }
                else {
                    val source = ImageDecoder.createSource(activity!!.contentResolver, uri)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    this.photo = bitmap
                }
            }

            addDbEntry()
        }
    }

    private fun initFoundEntryListener() {
        db.collection(FOUND_FIREBASE_ENTRY).get()
            .addOnSuccessListener {result ->
                for (document in result)
                    foundAdapter.addEntry(document.toObject())
            }
    }

    private fun isPermissionAllowed(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(activity!!.applicationContext, permissionType) == PackageManager.PERMISSION_GRANTED
    }

    private fun askForPermissions(permissionType: String, code: Int): Boolean {
        if (!isPermissionAllowed(permissionType)) {
            if (shouldShowRequestPermissionRationale(permissionType)) {
                showPermissionDeniedDialog()
            } else {
                requestPermissions(arrayOf(permissionType), code)
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makePhotoBtnClicked()
                }
                else {
                    askForPermissions(Manifest.permission.CAMERA, REQUEST_CODE_CAMERA)
                }
                return
            }

            REQUEST_CODE_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickPhotoBtnClicked()
                }
                else {
                    askForPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_CODE_STORAGE)
                }
                return
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(activity)
            .setTitle("Permission denied")
            .setMessage("Permission is denied. Please allow camera access from App Settings!")
            .setPositiveButton("App Settings"
            ) { _, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", activity!!.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun foundDbEntryClicked(item: FoundDbEntry) {
        findNavController().navigate(FoundMainFragmentDirections.actionFoundMainFragmentToFoundDetailsFragment(item.chipNum, item.breed, item.sex, item.foundAt, item.additionalInfo, item.photo))
    }
}
