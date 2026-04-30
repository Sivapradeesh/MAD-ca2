package com.example.madlabca2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.madlabca2.adapter.ParticipantAdapter
import com.example.madlabca2.databinding.ActivityParticipantListBinding
import com.example.madlabca2.model.Participant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ParticipantListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParticipantListBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: ParticipantAdapter
    private val participantList = mutableListOf<Participant>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParticipantListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        fetchParticipants()
        setupSearch()

        binding.toolbarList.setNavigationOnClickListener {
            finish()
        }

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = ParticipantAdapter(
            participantList,
            onEditClick = { participant -> navigateToEdit(participant) },
            onDeleteClick = { participant -> showDeleteConfirmation(participant) }
        )
        binding.rvParticipants.layoutManager = LinearLayoutManager(this)
        binding.rvParticipants.adapter = adapter
    }

    private fun fetchParticipants() {
        binding.progressBarList.visibility = View.VISIBLE
        
        db.collection("Participants")
            .orderBy("participantName", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    binding.progressBarList.visibility = View.GONE
                    Toast.makeText(this, "Listen failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                participantList.clear()
                snapshots?.let {
                    for (doc in it) {
                        val participant = doc.toObject(Participant::class.java)
                        participantList.add(participant)
                    }
                }

                binding.progressBarList.visibility = View.GONE
                adapter.updateList(participantList)
                updateSummaryCards()

                if (participantList.isEmpty()) {
                    binding.tvEmptyState.visibility = View.VISIBLE
                } else {
                    binding.tvEmptyState.visibility = View.GONE
                }
            }
    }

    private fun updateSummaryCards() {
        binding.cardTotal.tvSummaryCount.text = participantList.size.toString()
        binding.cardTotal.tvSummaryLabel.text = "Total"
        
        val confirmed = participantList.size // In Firestore you could have a status field
        val pending = 0
        
        binding.cardConfirmed.tvSummaryCount.text = confirmed.toString()
        binding.cardConfirmed.tvSummaryLabel.text = "Confirmed"
        binding.cardConfirmed.ivSummaryIcon.setImageResource(android.R.drawable.checkbox_on_background)
        
        binding.cardPending.tvSummaryCount.text = pending.toString()
        binding.cardPending.tvSummaryLabel.text = "Pending"
        binding.cardPending.ivSummaryIcon.setImageResource(android.R.drawable.ic_menu_recent_history)
        
        binding.cardCancelled.tvSummaryCount.text = "0"
        binding.cardCancelled.tvSummaryLabel.text = "Cancelled"
        binding.cardCancelled.ivSummaryIcon.setImageResource(android.R.drawable.ic_delete)
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun navigateToEdit(participant: Participant) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("PARTICIPANT_ID", participant.id)
            putExtra("NAME", participant.participantName)
            putExtra("EMAIL", participant.email)
            putExtra("COLLEGE", participant.collegeName)
            putExtra("MOBILE", participant.mobileNumber)
            putExtra("EVENT", participant.eventName)
            putExtra("TEAM_SIZE", participant.teamSize)
        }
        startActivity(intent)
    }

    private fun showDeleteConfirmation(participant: Participant) {
        AlertDialog.Builder(this)
            .setTitle("Delete Registration")
            .setMessage("Are you sure you want to delete ${participant.participantName}?")
            .setPositiveButton("Delete") { _, _ ->
                deleteParticipant(participant.id!!)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteParticipant(id: String) {
        db.collection("Participants").document(id).delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Participant deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
