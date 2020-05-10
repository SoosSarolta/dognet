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
import kotlinx.android.synthetic.main.dogs_in_training_db_entry_list_item.view.*

class DogsInTrainingAdapter(private val context: Context, private val clickListener: (TrainerDbEntry) -> Unit): RecyclerView.Adapter<DogsInTrainingAdapter.ViewHolder>() {

    private val petsList: MutableList<TrainerDbEntry> = mutableListOf()

    private var flag = false

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvChip: TextView = itemView.tvChip
        val tvName: TextView = itemView.tvName

        fun bind(item: TrainerDbEntry, clickListener: (TrainerDbEntry) -> Unit) {
            itemView.setOnClickListener {
                clickListener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        flag = false
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dogs_in_training_db_entry_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tmpEntry = petsList[position]
        holder.tvChip.text = tmpEntry.chipNum
        holder.tvName.text = tmpEntry.petName

        holder.bind(petsList[position], clickListener)

        setAnimation(holder.itemView)
    }

    override fun getItemCount() = petsList.size

    fun addEntry(trainerDbEntry: TrainerDbEntry?) {
        trainerDbEntry ?: return

        petsList.add(trainerDbEntry)

        flag = true

        notifyDataSetChanged()
    }

    private fun setAnimation(viewToAnimate: View) {
        if (!flag) {
            val animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
        }
    }
}