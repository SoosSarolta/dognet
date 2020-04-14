package hu.bme.aut.dognet.trainer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.trainer.model.TrainerDbEntry
import kotlinx.android.synthetic.main.vet_db_etry_list_item.view.*

class TrainerAdapter(private val context: Context, private val clickListener: (TrainerDbEntry) -> Unit): RecyclerView.Adapter<TrainerAdapter.ViewHolder>() {

    private val petsList: MutableList<TrainerDbEntry> = mutableListOf()
    private var lastPosition = -1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvChipNum: TextView = itemView.tvChipNum
        val tvPetName: TextView = itemView.tvPetName
        val tvOwnerName: TextView = itemView.tvOwnerName

        fun bind(item: TrainerDbEntry, clickListener: (TrainerDbEntry) -> Unit) {
            itemView.setOnClickListener {
                clickListener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trainer_db_entry_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tmpEntry = petsList[position]
        holder.tvChipNum.text = tmpEntry.chipNum
        holder.tvPetName.text = tmpEntry.petName
        holder.tvOwnerName.text = tmpEntry.ownerName

        holder.bind(petsList[position], clickListener)

        setAnimation(holder.itemView, position)
    }

    override fun getItemCount() = petsList.size

    fun addEntry(trainerDbEntry: TrainerDbEntry?) {
        trainerDbEntry ?: return

        petsList.add(trainerDbEntry)
        notifyDataSetChanged()
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }
}