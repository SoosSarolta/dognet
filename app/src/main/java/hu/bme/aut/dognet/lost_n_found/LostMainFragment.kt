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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hu.bme.aut.dognet.MainActivity
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.dialog_fragment.lost_n_found.AddImageDialogFragment
import hu.bme.aut.dognet.dialog_fragment.lost_n_found.LostAndFoundDialogFragment
import hu.bme.aut.dognet.dialog_fragment.lost_n_found.LostPetDataFormDialogFragment
import hu.bme.aut.dognet.lost_n_found.adapter.LostAdapter
import hu.bme.aut.dognet.lost_n_found.model.LostDbEntry
import hu.bme.aut.dognet.util.*
import kotlinx.android.synthetic.main.fragment_lost_main.*
import java.io.ByteArrayOutputStream

class LostMainFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var lostAdapter: LostAdapter

    private lateinit var entry: HashMap<String, String?>
    private lateinit var lostEntry: LostDbEntry

    private lateinit var chip: String
    private lateinit var petName: String
    private lateinit var breed: String
    private lateinit var sex: String
    private lateinit var ownerName: String
    private lateinit var phone: String
    private lateinit var lastSeen: String
    private lateinit var extraInfo: String

    private var photo: Bitmap? = null
    private var photoString: String? = null

    private var imageFromGallery = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lost_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = Firebase.firestore

        lostAdapter = LostAdapter(activity!!.applicationContext) { item: LostDbEntry -> lostDbEntryClicked(item) }
        recyclerView.layoutManager = LinearLayoutManager(activity).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        recyclerView.adapter = lostAdapter

        (activity as MainActivity).setDrawerEnabled(false)

        fab.setOnClickListener {
            val dialogFragment = LostPetDataFormDialogFragment()
            (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "lost_dialog") }
        }

        initLostEntryListener()
    }

    private fun addDbEntry(callbackExistsChip: Callback, callbackNotExistsChip: Callback,
                           callbackExistsNoChip: Callback, callbackNotExistsNoChip: Callback) {
        val baos =  ByteArrayOutputStream()
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
            "petName" to this.petName,
            "breed" to this.breed,
            "sex" to this.sex,
            "ownerName" to this.ownerName,
            "phoneNum" to this.phone,
            "lastSeen" to this.lastSeen,
            "additionalInfo" to this.extraInfo,
            "photo" to this.photoString
        )

        lostEntry = LostDbEntry.create()
        lostEntry.chipNum = entry["chipNum"].toString()
        lostEntry.petName = entry["petName"].toString()
        lostEntry.breed = entry["breed"].toString()
        lostEntry.sex = entry["sex"].toString()
        lostEntry.ownerName = entry["ownerName"].toString()
        lostEntry.phoneNum = entry["phoneNum"].toString()
        lostEntry.lastSeen = entry["lastSeen"].toString()
        lostEntry.additionalInfo = entry["additionalInfo"].toString()
        lostEntry.photo = entry["photo"].toString()

        if (this.chip == "-") {
            val ref = db.collection(FOUND_FIREBASE_ENTRY)
            val query1 = ref.whereEqualTo("sex", this.sex)
            val query2 = query1.whereEqualTo("breed", this.breed)
            query2.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result!!.isEmpty) {
                        callbackNotExistsNoChip.onCallback()
                    }
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
        else {
            val ref = db.collection(FOUND_FIREBASE_ENTRY)
            val query = ref.whereEqualTo("chipNum", this.chip)
            query.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result!!.isEmpty) {
                        callbackNotExistsChip.onCallback()
                    }
                    else {
                        for (dsnap in it.result!!) {
                            val tempChip = dsnap.getString("chipNum")
                            if (tempChip.equals(this.chip)) {
                                callbackExistsChip.onCallback()
                            }
                        }
                    }
                }
            }
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
        (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "photo_dialog") }
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
        addDbEntry(object: Callback {
            override fun onCallback() {
                db.collection(FOUND_FIREBASE_ENTRY).document(chip).get()
                    .addOnCompleteListener {task ->
                        if (task.isSuccessful) {
                            if (task.result!!.exists()) {
                                val c = task.result!!.getString("chipNum")
                                val s = task.result!!.getString("sex")
                                val b = task.result!!.getString("breed")
                                val f = task.result!!.getString("foundAt")
                                val p = task.result!!.getString("phone")
                                val a = task.result!!.getString("additionalInfo")

                                val dialogFragment = LostAndFoundDialogFragment()

                                val args = Bundle()
                                args.putString("chipNum", c)
                                args.putString("sex", s)
                                args.putString("breed", b)
                                args.putString("foundAt", f)
                                args.putString("phone", p)
                                args.putString("additionalInfo", a)

                                dialogFragment.arguments = args

                                (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "l_n_f_dialog") }
                            }
                        }
                    }
            }
        }, object: Callback {
            override fun onCallback() {
                db.collection(LOST_FIREBASE_ENTRY).document(chip).set(entry)
                    .addOnSuccessListener {
                        Toast.makeText(activity, "Entry added to database!", Toast.LENGTH_LONG).show()
                        lostAdapter.addEntry(lostEntry)
                        lostAdapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity, "An error happened!", Toast.LENGTH_LONG).show()
                    }
            }
        }, object: Callback {
            override fun onCallback() {
                db.collection(FOUND_FIREBASE_ENTRY).document("no chip - " + sex + " < > " + breed).get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (task.result!!.exists()) {
                                val s = task.result!!.getString("sex")
                                val b = task.result!!.getString("breed")
                                val f = task.result!!.getString("foundAt")
                                val p = task.result!!.getString("phone")
                                val a = task.result!!.getString("additionalInfo")

                                val dialogFragment = LostAndFoundDialogFragment()

                                val args = Bundle()
                                args.putString("chipNum", " - ")
                                args.putString("sex", s)
                                args.putString("breed", b)
                                args.putString("foundAt", f)
                                args.putString("phone", p)
                                args.putString("additionalInfo", a)

                                dialogFragment.arguments = args

                                (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "l_n_f_dialog") }
                            }
                        }
                    }
            }
        }, object: Callback {
            override fun onCallback() {
                db.collection(LOST_FIREBASE_ENTRY).document("no chip - " + sex + " < > " + breed).set(entry)
                    .addOnSuccessListener {
                        Toast.makeText(activity, "Entry added to database!", Toast.LENGTH_LONG).show()
                        lostAdapter.addEntry(lostEntry)
                        lostAdapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity, "An error happened!", Toast.LENGTH_LONG).show()
                    }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            this.photo = imageBitmap

            addDbEntry(object: Callback {
                override fun onCallback() {
                    db.collection(FOUND_FIREBASE_ENTRY).document(chip).get()
                        .addOnCompleteListener {task ->
                            if (task.isSuccessful) {
                                if (task.result!!.exists()) {
                                    val c = task.result!!.getString("chipNum")
                                    val s = task.result!!.getString("sex")
                                    val b = task.result!!.getString("breed")
                                    val f = task.result!!.getString("foundAt")
                                    val p = task.result!!.getString("phone")
                                    val a = task.result!!.getString("additionalInfo")

                                    val dialogFragment = LostAndFoundDialogFragment()

                                    val args = Bundle()
                                    args.putString("chipNum", c)
                                    args.putString("sex", s)
                                    args.putString("breed", b)
                                    args.putString("foundAt", f)
                                    args.putString("phone", p)
                                    args.putString("additionalInfo", a)

                                    dialogFragment.arguments = args

                                    (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "l_n_f_dialog") }
                                }
                            }
                        }
                }
            }, object: Callback {
                override fun onCallback() {
                    db.collection(LOST_FIREBASE_ENTRY).document(chip).set(entry)
                        .addOnSuccessListener {
                            Toast.makeText(activity, "Entry added to database!", Toast.LENGTH_LONG).show()
                            lostAdapter.addEntry(lostEntry)
                            lostAdapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener {
                            Toast.makeText(activity, "An error happened!", Toast.LENGTH_LONG).show()
                        }
                }
            }, object: Callback {
                override fun onCallback() {
                    db.collection(FOUND_FIREBASE_ENTRY).document("no chip - $sex < > $breed").get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                if (task.result!!.exists()) {
                                    val s = task.result!!.getString("sex")
                                    val b = task.result!!.getString("breed")
                                    val f = task.result!!.getString("foundAt")
                                    val p = task.result!!.getString("phone")
                                    val a = task.result!!.getString("additionalInfo")

                                    val dialogFragment = LostAndFoundDialogFragment()

                                    val args = Bundle()
                                    args.putString("chipNum", " - ")
                                    args.putString("sex", s)
                                    args.putString("breed", b)
                                    args.putString("foundAt", f)
                                    args.putString("phone", p)
                                    args.putString("additionalInfo", a)

                                    dialogFragment.arguments = args

                                    (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "l_n_f_dialog") }
                                }
                            }
                        }
                }
            }, object: Callback {
                override fun onCallback() {
                    db.collection(LOST_FIREBASE_ENTRY).document("no chip - $sex < > $breed").set(entry)
                        .addOnSuccessListener {
                            Toast.makeText(activity, "Entry added to database!", Toast.LENGTH_LONG).show()
                            lostAdapter.addEntry(lostEntry)
                            lostAdapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener {
                            Toast.makeText(activity, "An error happened!", Toast.LENGTH_LONG).show()
                        }
                }
            })
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

            addDbEntry(object: Callback {
                override fun onCallback() {
                    db.collection(FOUND_FIREBASE_ENTRY).document(chip).get()
                        .addOnCompleteListener {task ->
                            if (task.isSuccessful) {
                                if (task.result!!.exists()) {
                                    val c = task.result!!.getString("chipNum")
                                    val s = task.result!!.getString("sex")
                                    val b = task.result!!.getString("breed")
                                    val f = task.result!!.getString("foundAt")
                                    val p = task.result!!.getString("phone")
                                    val a = task.result!!.getString("additionalInfo")

                                    val dialogFragment = LostAndFoundDialogFragment()

                                    val args = Bundle()
                                    args.putString("chipNum", c)
                                    args.putString("sex", s)
                                    args.putString("breed", b)
                                    args.putString("foundAt", f)
                                    args.putString("phone", p)
                                    args.putString("additionalInfo", a)

                                    dialogFragment.arguments = args

                                    (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "l_n_f_dialog") }
                                }
                            }
                        }
                }
            }, object: Callback {
                override fun onCallback() {
                    db.collection(LOST_FIREBASE_ENTRY).document(chip).set(entry)
                        .addOnSuccessListener {
                            Toast.makeText(activity, "Entry added to database!", Toast.LENGTH_LONG).show()
                            lostAdapter.addEntry(lostEntry)
                            lostAdapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener {
                            Toast.makeText(activity, "An error happened!", Toast.LENGTH_LONG).show()
                        }
                }
            }, object: Callback {
                override fun onCallback() {
                    db.collection(FOUND_FIREBASE_ENTRY).document("no chip - $sex < > $breed").get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                if (task.result!!.exists()) {
                                    val s = task.result!!.getString("sex")
                                    val b = task.result!!.getString("breed")
                                    val f = task.result!!.getString("foundAt")
                                    val p = task.result!!.getString("phone")
                                    val a = task.result!!.getString("additionalInfo")

                                    val dialogFragment = LostAndFoundDialogFragment()

                                    val args = Bundle()
                                    args.putString("chipNum", " - ")
                                    args.putString("sex", s)
                                    args.putString("breed", b)
                                    args.putString("foundAt", f)
                                    args.putString("phone", p)
                                    args.putString("additionalInfo", a)

                                    dialogFragment.arguments = args

                                    (activity as MainActivity).supportFragmentManager.let { dialogFragment.show(it, "l_n_f_dialog") }
                                }
                            }
                        }
                }
            }, object: Callback {
                override fun onCallback() {
                    db.collection(LOST_FIREBASE_ENTRY).document("no chip - $sex < > $breed").set(entry)
                        .addOnSuccessListener {
                            Toast.makeText(activity, "Entry added to database!", Toast.LENGTH_LONG).show()
                            lostAdapter.addEntry(lostEntry)
                            lostAdapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener {
                            Toast.makeText(activity, "An error happened!", Toast.LENGTH_LONG).show()
                        }
                }
            })
        }
    }

    private fun initLostEntryListener() {
        db.collection(LOST_FIREBASE_ENTRY).get()
            .addOnSuccessListener { result ->
                for (document in result)
                    lostAdapter.addEntry(document.toObject())
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
