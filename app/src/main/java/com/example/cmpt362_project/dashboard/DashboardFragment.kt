package com.example.cmpt362_project.dashboard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cmpt362_project.R
import com.example.cmpt362_project.databinding.FragmentDashboardBinding
import com.example.cmpt362_project.property.PropertyAdapter
import com.example.cmpt362_project.property.PropertyViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var propertyAdapter: PropertyAdapter
    private val propertyViewModel: PropertyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize RecyclerView and Adapter
        propertyAdapter = PropertyAdapter(emptyList())
        binding.propertyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.propertyRecyclerView.adapter = propertyAdapter

        // Observe the properties data in ViewModel
        propertyViewModel.properties.observe(viewLifecycleOwner) { properties ->
            propertyAdapter = PropertyAdapter(properties)
            binding.propertyRecyclerView.adapter = propertyAdapter
        }

        // Implement search functionality
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                propertyViewModel.searchProperties(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

