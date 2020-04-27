package hu.bme.aut.dognet.trainer.model

class TrainingsDbEntry {
    companion object Factory {
        fun create(): TrainingsDbEntry = TrainingsDbEntry()
    }

    var date: String? = null
    var group: String? = null
    var pets: MutableList<TrainerDbEntry> = ArrayList()
}