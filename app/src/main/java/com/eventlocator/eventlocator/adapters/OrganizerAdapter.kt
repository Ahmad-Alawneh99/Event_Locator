package com.eventlocator.eventlocator.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eventlocator.eventlocator.data.Organizer
import com.eventlocator.eventlocator.databinding.OrganizerBinding
import com.eventlocator.eventlocator.ui.OrganizerProfileActivity

class OrganizerAdapter(private val organizers: ArrayList<Organizer>): RecyclerView.Adapter<OrganizerAdapter.OrganizerViewHolder>() {

    lateinit var context: Context

    inner class OrganizerViewHolder(var binding: OrganizerBinding): RecyclerView.ViewHolder(binding.root){

        init{
            binding.root.setOnClickListener {
                val intent = Intent(context, OrganizerProfileActivity::class.java)
                intent.putExtra("organizerID", binding.tvOrganizerID.text.toString().toLong())
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrganizerViewHolder{
        val binding = OrganizerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return OrganizerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrganizerViewHolder, position: Int) {
        holder.binding.tvOrganizerID.text = organizers[position].id.toString()
        holder.binding.tvOrganizerName.text = organizers[position].name
        holder.binding.tvFollowers.text = organizers[position].numberOfFollowers.toString()
    }

    override fun getItemCount(): Int = organizers.size
}