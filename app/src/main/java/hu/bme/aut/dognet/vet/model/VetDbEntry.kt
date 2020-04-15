package hu.bme.aut.dognet.vet.model

import java.util.*
import kotlin.collections.ArrayList

class VetDbEntry {
    companion object Factory {
        fun create(): VetDbEntry = VetDbEntry()
    }

    var chipNum: String? = null
    var petName: String? = null
    var breed: String? = null
    var sex: String? = null
    var dob: String? = null
    var ownerName: String? = null
    var ownerAddress: String? = null
    var phoneNum: String? = null
    var vaccinations: MutableMap<String, String> = HashMap()
    var medRecord: MutableList<String> = ArrayList()
}