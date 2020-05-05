package hu.bme.aut.dognet.lost_n_found.model

class FoundDbEntry {
    companion object Factory {
        fun create(): FoundDbEntry = FoundDbEntry()
    }

    var chipNum: String? = null
    var breed: String? = null
    var sex: String? = null
    var foundAt: String? = null

    var photo: String? = null

    var additionalInfo: String? = null
}