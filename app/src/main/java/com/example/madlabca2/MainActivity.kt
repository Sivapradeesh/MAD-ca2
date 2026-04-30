package com.example.madlabca2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.madlabca2.databinding.ActivityMainBinding
import com.example.madlabca2.model.Participant
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseFirestore
    private var participantId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        setupEventSpinner()

        // Check if editing existing participant
        intent.getStringExtra("PARTICIPANT_ID")?.let { id ->
            participantId = id
            populateFields()
            binding.btnRegister.text = "UPDATE"
        }

        binding.btnRegister.setOnClickListener {
            saveParticipant()
        }

        binding.btnClear.setOnClickListener {
            clearFields()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnViewList.setOnClickListener {
            startActivity(Intent(this, ParticipantListActivity::class.java))
        }
    }

    private fun setupEventSpinner() {
        val events = arrayOf("Technical Quiz", "Paper Presentation", "Coding Contest", "Web Design", "Project Expo")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, events)
        binding.spinnerEvent.setAdapter(adapter)
    }

    private fun populateFields() {
        binding.etName.setText(intent.getStringExtra("NAME"))
        binding.etEmail.setText(intent.getStringExtra("EMAIL"))
        binding.etCollege.setText(intent.getStringExtra("COLLEGE"))
        binding.etMobile.setText(intent.getStringExtra("MOBILE"))
        binding.spinnerEvent.setText(intent.getStringExtra("EVENT"), false)
        binding.etTeamSize.setText(intent.getIntExtra("TEAM_SIZE", 1).toString())
    }

    private fun saveParticipant() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val college = binding.etCollege.text.toString().trim()
        val mobile = binding.etMobile.text.toString().trim()
        val event = binding.spinnerEvent.text.toString().trim()
        val teamSizeStr = binding.etTeamSize.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || college.isEmpty() || mobile.isEmpty() || event.isEmpty() || teamSizeStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val teamSize = teamSizeStr.toIntOrNull() ?: 1
        binding.progressBar.visibility = View.VISIBLE

        val id = participantId ?: db.collection("Participants").document().id
        val participant = Participant(id, name, email, college, mobile, event, teamSize)

        db.collection("Participants").document(id).set(participant)
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                val msg = if (participantId == null) "Registered Successfully" else "Updated Successfully"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                if (participantId != null) finish() else clearFields()
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        binding.etName.text?.clear()
        binding.etEmail.text?.clear()
        binding.etCollege.text?.clear()
        binding.etMobile.text?.clear()
        binding.etTeamSize.text?.clear()
        binding.spinnerEvent.setText("Select event", false)
        participantId = null
        binding.btnRegister.text = "REGISTER"
    }
}