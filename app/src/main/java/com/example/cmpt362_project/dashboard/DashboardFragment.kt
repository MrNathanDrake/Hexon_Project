package com.example.cmpt362_project.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cmpt362_project.R
import com.example.cmpt362_project.databinding.FragmentDashboardBinding
import com.example.cmpt362_project.inbox.InboxFragment
import com.example.cmpt362_project.property.Property
import com.example.cmpt362_project.property.PropertyAdapter
import com.example.cmpt362_project.property.PropertyViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

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
        Log.d("DashboardFragment", "onCreateView called")
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize RecyclerView and Adapter
        propertyAdapter = PropertyAdapter(
            properties = emptyList(),
            onDeleteClick = { property -> deleteDialog(property)},
            onStatusChange = { property, newStatus ->
                propertyViewModel.updatePropertyStatus(property, newStatus)
            },
            onViewClick = {
                val navController = findNavController()
                val navView: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)

                // Clear the back stack to avoid duplicate fragments
                navController.popBackStack(R.id.navigation_dashboard, true)
                navController.navigate(R.id.navigation_inbox)

                // Synchronize BottomNavigationView
                navView.selectedItemId = R.id.navigation_inbox
            }
        )
        binding.propertyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.propertyRecyclerView.adapter = propertyAdapter

        // Observe the properties data in ViewModel
        propertyViewModel.properties.observe(viewLifecycleOwner) { properties ->
            propertyAdapter.updateProperties(properties)
        }

        binding.listButton.setOnClickListener {
            propertyViewModel.loadProperties()

            Toast.makeText(requireContext(), "Showing all properties", Toast.LENGTH_SHORT).show()
        }

        // Implement search functionality
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                propertyViewModel.searchProperties(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val spinnerItems = resources.getStringArray(R.array.property_status_options).toList()
        val adapter = CustomSpinnerAdapter(requireContext(), spinnerItems)
        binding.filterSpinner.adapter = adapter

        // Implement filter functionality
        binding.filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                val query = binding.searchEditText.text.toString()
                val filter = parent?.getItemAtPosition(position).toString()
                propertyViewModel.searchProperties(query, filter)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // implement map button
        binding.mapButton.setOnClickListener {
            val postalCodes = propertyViewModel.properties.value?.mapNotNull { property ->
                property.postalCode
            } ?: emptyList()

            if (postalCodes.isEmpty()) {
                Toast.makeText(requireContext(), "No properties available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 将所有邮政编码传递到 MapActivity
            val intent = Intent(requireContext(), MapActivity::class.java)
            intent.putStringArrayListExtra("postal_codes", ArrayList(postalCodes))
            startActivity(intent)
        }

        return root
    }

    private fun deleteDialog(property: Property) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Property")
        builder.setMessage("Are you sure to delete this property?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            propertyViewModel.deleteProperty(property)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class CustomSpinnerAdapter(
    private val context: Context,
    private val items: List<String>) : ArrayAdapter<String>(context, R.layout.spinner_item_with_icon, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.spinner_item_with_icon, parent, false)
        val icon = view.findViewById<ImageView>(R.id.spinnerIcon)
        val text = view.findViewById<TextView>(R.id.spinnerText)

        // Set text and icon
        text.text = items[position]
        icon.setImageResource(R.drawable.filter) // Replace with different icons if needed

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}
