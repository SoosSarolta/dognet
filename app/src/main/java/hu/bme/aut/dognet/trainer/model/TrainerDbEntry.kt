package hu.bme.aut.dognet.trainer.model

class TrainerDbEntry {
    companion object Factory {
        fun create(): TrainerDbEntry = TrainerDbEntry()
    }

    var chipNum: String? = null
    var petName: String? = null
    var breed: String? = null
    var ownerName: String? = null
    var phoneNum: String? = null
    var group: String? = null
    var trainings: List<String> = ArrayList()
    var notes: String? = null
}