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
import kotlinx.android.synthetic.main.vet_db_etry_list_item.view.*

class VetAdapter(private val context: Context) : RecyclerView.Adapter<VetAdapter.ViewHolder>() {

    private val petsList: MutableList<VetDbEntry> = mutableListOf()
    private var lastPosition = -1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvChipNum: TextView = itemView.tvChipNum
        val tvPetName: TextView = itemView.tvPetName
        val tvOwnerName: TextView = itemView.tvOwnerName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vet_db_etry_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tmpEntry = petsList[position]
        holder.tvChipNum.text = tmpEntry.chipNum
        holder.tvPetName.text = tmpEntry.petName
        holder.tvOwnerName.text = tmpEntry.ownerName

        setAnimation(holder.itemView, position)
    }

    override fun getItemCount() = petsList.size

    fun addEntry(vetDbEntry: VetDbEntry?) {
        vetDbEntry ?: return

        petsList.add(vetDbEntry)
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