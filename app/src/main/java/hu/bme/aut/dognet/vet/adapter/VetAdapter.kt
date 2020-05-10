package hu.bme.aut.dognet.vet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.dognet.R
import hu.bme.aut.dognet.vet.model.VetDbEntry
import kotlinx.android.synthetic.main.vet_db_entry_list_item.view.*

class VetAdapter(private val context: Context, private val clickListener: (VetDbEntry) -> Unit) : RecyclerView.Adapter<VetAdapter.ViewHolder>() {

    private val petsList: MutableList<VetDbEntry> = mutableListOf()

    private var flag = false

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvChipNum: TextView = itemView.tvChipNum
        val tvPetName: TextView = itemView.tvPetName
        val tvOwnerName: TextView = itemView.tvOwnerName

        fun bind(item: VetDbEntry, clickListener: (VetDbEntry) -> Unit) {
           itemView.setOnClickListener {
               clickListener(item)
           }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        flag = false
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vet_db_entry_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tmpEntry = petsList[position]
        holder.tvChipNum.text = tmpEntry.chipNum
        holder.tvPetName.text = tmpEntry.petName
        holder.tvOwnerName.text = tmpEntry.ownerName

        holder.bind(petsList[position], clickListener)

        setAnimation(holder.itemView)
    }

    override fun getItemCount() = petsList.size

    fun addEntry(vetDbEntry: VetDbEntry?) {
        vetDbEntry ?: return

        petsList.add(vetDbEntry)

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