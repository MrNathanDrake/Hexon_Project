package com.example.cmpt362_project.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cmpt362_project.R
import com.example.cmpt362_project.authority.SignInActivity
import com.example.cmpt362_project.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var isEditing = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view  = binding.root

        // Loading User Information from Firebase
        loadUserProfile()

        binding.profileEditButton.setOnClickListener {
            if (isEditing) {
                saveUserProfile()
            } else {
                enableEditingMode()
            }
        }

        // Setting the Exit Button Function
        binding.logoutButton.setOnClickListener {
            logOut()
        }

        return view
    }

    private fun loadUserProfile() {
        val user = FirebaseAuth.getInstance().currentUser
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("user").child(user?.uid ?: "")

        userRef.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val name = dataSnapshot.child("name").value as? String ?: "N/A"
                val email = dataSnapshot.child("email").value as? String ?: "N/A"

                binding.nameEdit.setText(name)
                binding.emailEdit.setText(email)

                disableEditingMode()

            } else {
                Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enableEditingMode() {
        binding.nameEdit.isEnabled = true

        binding.profileEditButton.text = "Save"

        isEditing = true
    }

    private fun disableEditingMode() {
        binding.nameEdit.isEnabled = false
        binding.nameEdit.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

        binding.profileEditButton.text = "Edit"

        isEditing = false
    }

    private fun saveUserProfile() {
        val user = FirebaseAuth.getInstance().currentUser
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("user").child(user?.uid ?: "")

        val updatedName = binding.nameEdit.text.toString()

        val updates = mapOf(
            "name" to updatedName,
        )

        userRef.updateChildren(updates).addOnSuccessListener {
            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()

            disableEditingMode()
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(requireContext(), SignInActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}