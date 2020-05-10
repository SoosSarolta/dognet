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
import hu.bme.aut.dognet.dialog_fragment.lost_n_found.FoundAndLostDialogFragment
import hu.bme.aut.dognet.dialog_fragment.lost_n_found.FoundPetDataFormDialogFragment
import hu.bme.aut.dognet.lost_n_found.adapter.FoundAdapter
import hu.bme.aut.dognet.lost_n_found.model.FoundDbEntry
import hu.bme.aut.dognet.util.*
import kotlinx.android.synthetic.main.fragment_found_main.*
import java.io.ByteArrayOutputStream

class FoundMainFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var foundAdapter: FoundAdapter

    private lateinit var entry: HashMap<String, String?>
    private lateinit var foundEntry: FoundDbEntry

    private lateinit var chip: String
    private lateinit var breed: String
    private lateinit var sex: String
    private lateinit var phone: String
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
            reverseLayout = false
            stackFromEnd = false
        }
        recyclerView.adapter = foundAdapter

        (activity as MainActivity).setDrawerEnabled(false)

        fab.setOnClickListener {
            val dialogFragment = ChipReadDialogFragment()
            (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "dialog") }
        }

        initFoundEntryListener()
    }

    private fun addDbEntry(callbackExistsChip: Callback, callbackNotExistsChip: Callback,
                           callbackExistsNoChip: Callback, callbackNotExistsNoChip: Callback) {
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

        entry = hashMapOf(
            "chipNum" to this.chip,
            "breed" to this.breed,
            "sex" to this.sex,
            "phone" to this.phone,
            "foundAt" to this.foundAt,
            "additionalInfo" to this.extraInfo,
            "photo" to this.photoString
        )

        foundEntry = FoundDbEntry.create()
        foundEntry.chipNum = entry["chipNum"].toString()
        foundEntry.breed = entry["breed"].toString()
        foundEntry.sex = entry["sex"].toString()
        foundEntry.phone = entry["phone"].toString()
        foundEntry.foundAt = entry["foundAt"].toString()
        foundEntry.additionalInfo = entry["additionalInfo"].toString()
        foundEntry.photo = entry["photo"].toString()

        if (!noChip) {
            val ref = db.collection(LOST_FIREBASE_ENTRY)
            val query = ref.whereEqualTo("chipNum", this.chip)
            query.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result!!.isEmpty)
                        callbackNotExistsChip.onCallback()
                    else {
                        for (dsnap in it.result!!) {
                            val tempChip = dsnap.getString("chipNum")
                            if (tempChip.equals(this.chip))
                                callbackExistsChip.onCallback()
                        }
                    }
                }
            }
        }
        else {
            val ref = db.collection(LOST_FIREBASE_ENTRY)
            val query1 = ref.whereEqualTo("sex", this.sex)
            val query2 = query1.whereEqualTo("breed", this.breed)
            query2.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result!!.isEmpty)
                        callbackNotExistsNoChip.onCallback()
                    else {
                        for (dsnap in it.result!!) {
                            val tempSex = dsnap.getString("sex")
                            if (tempSex.equals(this.sex)) {
                                val tempBreed = dsnap.getString("breed")
                                if (tempBreed.equals(this.breed)) {
                                    callbackExistsNoChip.onCallback()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun setData(breed: String, sex: String, phone: String, foundAt: String, extra: String) {
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

        this.phone = phone

        openAddImageDialogFragment()
    }

    fun chipRead(chip: String) {
        this.chip = chip
        openFoundDataForm()
    }

    fun noChipFound() {
        noChip = true
        this.chip = "-"
        openFoundDataForm()
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
        addDbEntry(object: Callback {
            override fun onCallback() {
                db.collection(LOST_FIREBASE_ENTRY).document(chip).get()
                    .addOnCompleteListener {task ->
                        if (task.isSuccessful) {
                            if (task.result!!.exists()) {
                                val c = task.result!!.getString("chipNum")
                                val s = task.result!!.getString("sex")
                                val b = task.result!!.getString("breed")
                                val l = task.result!!.getString("lastSeen")
                                val o = task.result!!.getString("ownerName")
                                val p = task.result!!.getString("phoneNum")
                                val a = task.result!!.getString("additionalInfo")

                                val dialogFragment = FoundAndLostDialogFragment()

                                val args = Bundle()
                                args.putString("chipNum", c)
                                args.putString("sex", s)
                                args.putString("breed", b)
                                args.putString("lastSeen", l)
                                args.putString("ownerName", o)
                                args.putString("phoneNum", p)
                                args.putString("additionalInfo", a)

                                dialogFragment.arguments = args

                                (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "f_n_l_dialog")}
                            }
                        }
                    }
            }
        }, object: Callback {
            override fun onCallback() {
                db.collection(FOUND_FIREBASE_ENTRY).document(chip).set(entry)
                    .addOnSuccessListener {
                        Toast.makeText(activity, "Entry added to database!", Toast.LENGTH_LONG).show()
                        foundAdapter.addEntry(foundEntry)
                        foundAdapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity, "An error happened!", Toast.LENGTH_LONG).show()
                    }
            }
        }, object: Callback {
            override fun onCallback() {
                db.collection(LOST_FIREBASE_ENTRY).document("no chip - " + sex + " < > " + breed).get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (task.result!!.exists()) {
                                val s = task.result!!.getString("sex")
                                val b = task.result!!.getString("breed")
                                val l = task.result!!.getString("lastSeen")
                                val o = task.result!!.getString("ownerName")
                                val p = task.result!!.getString("phoneNum")
                                val a = task.result!!.getString("additionalInfo")

                                val dialogFragment = FoundAndLostDialogFragment()

                                val args = Bundle()
                                args.putString("chipNum", " - ")
                                args.putString("sex", s)
                                args.putString("breed", b)
                                args.putString("lastSeen", l)
                                args.putString("ownerName", o)
                                args.putString("phoneNum", p)
                                args.putString("additionalInfo", a)

                                dialogFragment.arguments = args

                                (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "f_n_l_dialog") }
                            }
                        }
                    }
            }
        }, object: Callback {
            override fun onCallback() {
                db.collection(FOUND_FIREBASE_ENTRY).document("no chip - " + sex + " < > " + breed).set(entry)
                    .addOnSuccessListener {
                        Toast.makeText(activity, "Entry added to database!", Toast.LENGTH_LONG).show()
                        foundAdapter.addEntry(foundEntry)
                        foundAdapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity, "An error happened!", Toast.LENGTH_LONG).show()
                    }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            this.photo = imageBitmap

            addDbEntry(object: Callback {
                override fun onCallback() {
                    db.collection(LOST_FIREBASE_ENTRY).document(chip).get()
                        .addOnCompleteListener {task ->
                            if (task.isSuccessful) {
                                if (task.result!!.exists()) {
                                    val c = task.result!!.getString("chipNum")
                                    val s = task.result!!.getString("sex")
                                    val b = task.result!!.getString("breed")
                                    val l = task.result!!.getString("lastSeen")
                                    val o = task.result!!.getString("ownerName")
                                    val p = task.result!!.getString("phoneNum")
                                    val a = task.result!!.getString("additionalInfo")

                                    val dialogFragment = FoundAndLostDialogFragment()

                                    val args = Bundle()
                                    args.putString("chipNum", c)
                                    args.putString("sex", s)
                                    args.putString("breed", b)
                                    args.putString("lastSeen", l)
                                    args.putString("ownerName", o)
                                    args.putString("phoneNum", p)
                                    args.putString("additionalInfo", a)

                                    dialogFragment.arguments = args

                                    (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "f_n_l_dialog")}
                                }
                            }
                        }
                }
            }, object: Callback {
                override fun onCallback() {
                    db.collection(FOUND_FIREBASE_ENTRY).document(chip).set(entry)
                        .addOnSuccessListener {
                            Toast.makeText(activity, "Entry added to database!", Toast.LENGTH_LONG).show()
                            foundAdapter.addEntry(foundEntry)
                            foundAdapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener {
                            Toast.makeText(activity, "An error happened!", Toast.LENGTH_LONG).show()
                        }
                }
            }, object: Callback {
                override fun onCallback() {
                    db.collection(LOST_FIREBASE_ENTRY).document("no chip - $sex < > $breed").get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                if (task.result!!.exists()) {
                                    val s = task.result!!.getString("sex")
                                    val b = task.result!!.getString("breed")
                                    val l = task.result!!.getString("lastSeen")
                                    val o = task.result!!.getString("ownerName")
                                    val p = task.result!!.getString("phoneNum")
                                    val a = task.result!!.getString("additionalInfo")

                                    val dialogFragment = FoundAndLostDialogFragment()

                                    val args = Bundle()
                                    args.putString("chipNum", " - ")
                                    args.putString("sex", s)
                                    args.putString("breed", b)
                                    args.putString("lastSeen", l)
                                    args.putString("ownerName", o)
                                    args.putString("phoneNum", p)
                                    args.putString("additionalInfo", a)

                                    dialogFragment.arguments = args

                                    (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "f_n_l_dialog") }
                                }
                            }
                        }
                }
            }, object: Callback {
                override fun onCallback() {
                    db.collection(FOUND_FIREBASE_ENTRY).document("no chip - $sex < > $breed").set(entry)
                        .addOnSuccessListener {
                            Toast.makeText(activity, "Entry added to database!", Toast.LENGTH_LONG).show()
                            foundAdapter.addEntry(foundEntry)
                            foundAdapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener {
                            Toast.makeText(activity, "An error happened!", Toast.LENGTH_LONG).show()
                        }
                }
            })
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

            addDbEntry(object: Callback {
                override fun onCallback() {
                    db.collection(LOST_FIREBASE_ENTRY).document(chip).get()
                        .addOnCompleteListener {task ->
                            if (task.isSuccessful) {
                                if (task.result!!.exists()) {
                                    val c = task.result!!.getString("chipNum")
                                    val s = task.result!!.getString("sex")
                                    val b = task.result!!.getString("breed")
                                    val l = task.result!!.getString("lastSeen")
                                    val o = task.result!!.getString("ownerName")
                                    val p = task.result!!.getString("phoneNum")
                                    val a = task.result!!.getString("additionalInfo")

                                    val dialogFragment = FoundAndLostDialogFragment()

                                    val args = Bundle()
                                    args.putString("chipNum", c)
                                    args.putString("sex", s)
                                    args.putString("breed", b)
                                    args.putString("lastSeen", l)
                                    args.putString("ownerName", o)
                                    args.putString("phoneNum", p)
                                    args.putString("additionalInfo", a)

                                    dialogFragment.arguments = args

                                    (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "f_n_l_dialog")}
                                }
                            }
                        }
                }
            }, object: Callback {
                override fun onCallback() {
                    db.collection(FOUND_FIREBASE_ENTRY).document(chip).set(entry)
                        .addOnSuccessListener {
                            Toast.makeText(activity, "Entry added to database!", Toast.LENGTH_LONG).show()
                            foundAdapter.addEntry(foundEntry)
                            foundAdapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener {
                            Toast.makeText(activity, "An error happened!", Toast.LENGTH_LONG).show()
                        }
                }
            }, object: Callback {
                override fun onCallback() {
                    db.collection(LOST_FIREBASE_ENTRY).document("no chip - $sex < > $breed").get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                if (task.result!!.exists()) {
                                    val s = task.result!!.getString("sex")
                                    val b = task.result!!.getString("breed")
                                    val l = task.result!!.getString("lastSeen")
                                    val o = task.result!!.getString("ownerName")
                                    val p = task.result!!.getString("phoneNum")
                                    val a = task.result!!.getString("additionalInfo")

                                    val dialogFragment = FoundAndLostDialogFragment()

                                    val args = Bundle()
                                    args.putString("chipNum", " - ")
                                    args.putString("sex", s)
                                    args.putString("breed", b)
                                    args.putString("lastSeen", l)
                                    args.putString("ownerName", o)
                                    args.putString("phoneNum", p)
                                    args.putString("additionalInfo", a)

                                    dialogFragment.arguments = args

                                    (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "f_n_l_dialog") }
                                }
                            }
                        }
                }
            }, object: Callback {
                override fun onCallback() {
                    db.collection(FOUND_FIREBASE_ENTRY).document("no chip - $sex < > $breed").set(entry)
                        .addOnSuccessListener {
                            Toast.makeText(activity, "Entry added to database!", Toast.LENGTH_LONG).show()
                            foundAdapter.addEntry(foundEntry)
                            foundAdapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener {
                            Toast.makeText(activity, "An error happened!", Toast.LENGTH_LONG).show()
                        }
                }
            })
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
        findNavController().navigate(FoundMainFragmentDirections.actionFoundMainFragmentToFoundDetailsFragment(item.chipNum, item.breed, item.sex, item.foundAt, item.additionalInfo, item.photo, item.phone))
    }
}
