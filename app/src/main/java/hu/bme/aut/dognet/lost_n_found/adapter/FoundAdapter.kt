package hu.bme.aut.dognet.lost_n_found.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.lost_n_found.model.FoundDbEntry
import kotlinx.android.synthetic.main.found_db_entry_list_item.view.*

class FoundAdapter(private val context: Context, private val clickListener: (FoundDbEntry) -> Unit) : RecyclerView.Adapter<FoundAdapter.ViewHolder>() {

    private val petsList: MutableList<FoundDbEntry> = mutableListOf()

    private var flag = false

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvChipNum: TextView = itemView.tvChipNum
        val tvBreed: TextView = itemView.tvBreed
        val tvSex: TextView = itemView.tvSex

        fun bind(item: FoundDbEntry, clickListener: (FoundDbEntry) -> Unit) {
            itemView.setOnClickListener {
                clickListener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        flag = false
        val view = LayoutInflater.from(parent.context).inflate(R.layout.found_db_entry_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tmpEntry = petsList[position]
        holder.tvChipNum.text = tmpEntry.chipNum
        holder.tvBreed.text = tmpEntry.breed
        holder.tvSex.text = tmpEntry.sex

        holder.bind(petsList[position], clickListener)

        setAnimation(holder.itemView)
    }

    override fun getItemCount() = petsList.size

    fun addEntry(foundDbEntry: FoundDbEntry?) {
        foundDbEntry ?: return

        petsList.add(foundDbEntry)

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