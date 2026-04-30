package com.example.madlabca2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madlabca2.R
import com.example.madlabca2.model.Participant

class ParticipantAdapter(
    private var participants: List<Participant>,
    private val onEditClick: (Participant) -> Unit,
    private val onDeleteClick: (Participant) -> Unit
) : RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder>() {

    private var filteredList: List<Participant> = participants

    class ParticipantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvInitials: TextView = itemView.findViewById(R.id.tvInitials)
        val tvName: TextView = itemView.findViewById(R.id.tvCardName)
        val tvCollege: TextView = itemView.findViewById(R.id.tvCardCollege)
        val tvEvent: TextView = itemView.findViewById(R.id.tvCardEvent)
        val tvMobile: TextView = itemView.findViewById(R.id.tvCardMobile)
        val tvTeamSize: TextView = itemView.findViewById(R.id.tvCardTeamSize)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_participant, parent, false)
        return ParticipantViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        val participant = filteredList[position]
        
        // Get initials
        val name = participant.participantName ?: "NA"
        val initials = name.split(" ").filter { it.isNotEmpty() }.take(2).map { it[0] }.joinToString("").uppercase()
        holder.tvInitials.text = if (initials.isEmpty()) "NA" else initials
        
        holder.tvName.text = participant.participantName
        holder.tvCollege.text = participant.collegeName
        holder.tvEvent.text = "${participant.eventName} 2025"
        holder.tvMobile.text = participant.mobileNumber
        holder.tvTeamSize.text = "| Team Size: ${participant.teamSize}"

        holder.itemView.setOnClickListener { onEditClick(participant) }
        holder.btnDelete.setOnClickListener { onDeleteClick(participant) }
    }

    override fun getItemCount(): Int = filteredList.size

    fun updateList(newList: List<Participant>) {
        participants = newList
        filteredList = newList
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            participants
        } else {
            participants.filter {
                it.participantName?.contains(query, ignoreCase = true) == true ||
                it.collegeName?.contains(query, ignoreCase = true) == true
            }
        }
        notifyDataSetChanged()
    }
}
