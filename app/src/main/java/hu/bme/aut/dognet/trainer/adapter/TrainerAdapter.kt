package hu.bme.aut.dognet.trainer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.trainer.model.TrainingsDbEntry
import kotlinx.android.synthetic.main.trainer_db_entry_list_item.view.*

class TrainerAdapter(private val context: Context, private val clickListener: (TrainingsDbEntry) -> Unit): RecyclerView.Adapter<TrainerAdapter.ViewHolder>() {

    private val trainingList: MutableList<TrainingsDbEntry> = mutableListOf()

    private var flag = false

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.tvDate
        val tvGroup: TextView = itemView.tvGroup

        fun bind(item: TrainingsDbEntry, clickListener: (TrainingsDbEntry) -> Unit) {
            itemView.setOnClickListener {
                clickListener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        flag = false
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trainer_db_entry_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tmpEntry = trainingList[position]
        holder.tvDate.text = tmpEntry.date
        holder.tvGroup.text = tmpEntry.group

        holder.bind(trainingList[position], clickListener)

        setAnimation(holder.itemView)
    }

    override fun getItemCount() = trainingList.size

    fun addEntry(trainingEntry: TrainingsDbEntry?) {
        trainingEntry ?: return

        trainingList.add(trainingEntry)

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