package hu.bme.aut.dognet.lost_n_found.model

class LostDbEntry {
    companion object Factory {
        fun create(): LostDbEntry = LostDbEntry()
    }

    var chipNum: String? = null
    var petName: String? = null
    var breed: String? = null
    var sex: String? = null
    var ownerName: String? = null
    var phoneNum: String? = null
    var lastSeen: String? = null

    var photo: String? = null

    var additionalInfo: String? = null
}