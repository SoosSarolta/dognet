package hu.bme.aut.dognet.lost_n_found

import android.Manifest
import android.app.Activity.RESULT_OK
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
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.dognet.MainActivity
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.dialog_fragment.lost_n_found.AddImageDialogFragment
import hu.bme.aut.dognet.dialog_fragment.lost_n_found.LostPetDataFormDialogFragment
import hu.bme.aut.dognet.lost_n_found.adapter.LostAdapter
import hu.bme.aut.dognet.lost_n_found.model.LostDbEntry
import hu.bme.aut.dognet.util.DB
import hu.bme.aut.dognet.util.LOST_FIREBASE_ENTRY
import hu.bme.aut.dognet.util.REQUEST_CODE_CAMERA
import hu.bme.aut.dognet.util.REQUEST_CODE_STORAGE
import kotlinx.android.synthetic.main.fragment_lost_main.*
import java.io.ByteArrayOutputStream

// TODO link lost & found pages - if chip numbers are alike, notify user and show the appropriate item
// TODO replace deprecated fragment manager calls
// TODO migrate to firestore
class LostMainFragment : Fragment() {

    private lateinit var lostAdapter: LostAdapter

    private lateinit var entry: LostDbEntry

    private lateinit var chip: String
    private lateinit var petName: String
    private lateinit var breed: String
    private lateinit var sex: String
    private lateinit var ownerName: String
    private lateinit var phone: String
    private lateinit var lastSeen: String
    private lateinit var extraInfo: String

    private var photo: Bitmap? = null

    private var imageFromGallery = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lost_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lostAdapter = LostAdapter(activity!!.applicationContext) { item: LostDbEntry -> lostDbEntryClicked(item) }
        recyclerView.layoutManager = LinearLayoutManager(activity).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        recyclerView.adapter = lostAdapter

        (activity as MainActivity).setDrawerEnabled(false)

        fab.setOnClickListener {
            val dialogFragment = LostPetDataFormDialogFragment()
            fragmentManager?.let { dialogFragment.show(it, "lost_dialog") }
        }

        initLostEntryListener()
    }

    private fun addDbEntry() {
        DB.child(LOST_FIREBASE_ENTRY).push().key ?: return

        entry = LostDbEntry.create()

        entry.chipNum = this.chip
        entry.petName = this.petName
        entry.breed = this.breed
        entry.sex = this.sex
        entry.ownerName = this.ownerName
        entry.phoneNum = this.phone
        entry.lastSeen = this.lastSeen
        entry.additionalInfo = this.extraInfo

        val baos =  ByteArrayOutputStream()
        val imageEncoded: String
        if (this.photo != null) {
            if (imageFromGallery)
                this.photo!!.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            else
                this.photo!!.compress(Bitmap.CompressFormat.PNG, 100, baos)

            imageEncoded = android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.DEFAULT)
            entry.photo = imageEncoded
        }
        else
            entry.photo = null

        val pets: MutableMap<String, LostDbEntry> = HashMap()

        if (this.chip == "-") {
            // TODO - no chip in pet
        }
        else {
            pets[this.chip] = entry

            val ref = DB.child(LOST_FIREBASE_ENTRY)
            ref.updateChildren(pets as Map<String, Any>)
            Toast.makeText(this.activity!!, "Entry added to database!", Toast.LENGTH_LONG).show()
        }
    }

    fun setData(chip: String, petName: String, breed: String, sex: String, ownerName: String, phone: String, lastSeen: String, extra: String) {
        this.petName = petName
        this.breed = breed
        this.sex = sex
        this.ownerName = ownerName
        this.phone = phone

        if (chip == "")
            this.chip = "-"
        else
            this.chip = chip

        if (lastSeen == "")
            this.lastSeen = ""
        else
            this.lastSeen = lastSeen

        if (extra == "")
            this.extraInfo = "-"
        else
            this.extraInfo = extra

        openAddImageDialogFragment()
    }

    private fun openAddImageDialogFragment() {
        val dialogFragment = AddImageDialogFragment()
        fragmentManager?.let { dialogFragment.show(it, "photo_dialog") }
    }

    fun takePhotoBtnClicked() {
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

    fun choosePhotoBtnClicked() {
        imageFromGallery = true

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), REQUEST_CODE_STORAGE)
    }

    fun noPhotoBtnClicked() {
        this.photo = null
        addDbEntry()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            this.photo = imageBitmap

            addDbEntry()
        }

        if (requestCode == REQUEST_CODE_STORAGE && resultCode == RESULT_OK) {
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

    private fun initLostEntryListener() {
        FirebaseDatabase.getInstance().getReference(LOST_FIREBASE_ENTRY)
            .addChildEventListener(object: ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val newEntry = dataSnapshot.getValue<LostDbEntry>(LostDbEntry::class.java)
                    lostAdapter.addEntry(newEntry)
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) { }

                override fun onChildRemoved(p0: DataSnapshot) { }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) { }

                override fun onCancelled(p0: DatabaseError) { }
            })
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
                    takePhotoBtnClicked()
                }
                else {
                    askForPermissions(Manifest.permission.CAMERA, REQUEST_CODE_CAMERA)
                }
                return
            }

            REQUEST_CODE_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosePhotoBtnClicked()
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

    private fun lostDbEntryClicked(item: LostDbEntry) {
        findNavController().navigate(LostMainFragmentDirections.actionLostMainFragmentToLostDetailsFragment(item.chipNum, item.petName, item.breed, item.sex, item.ownerName, item.phoneNum, item.additionalInfo, item.photo, item.lastSeen))
    }
}
