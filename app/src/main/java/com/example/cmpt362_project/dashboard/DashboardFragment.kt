package com.example.cmpt362_project.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cmpt362_project.R
import com.example.cmpt362_project.databinding.FragmentDashboardBinding
import com.example.cmpt362_project.property.Property
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
        propertyAdapter = PropertyAdapter(
            properties = emptyList(),
            onDeleteClick = { property -> deleteDialog(property)},
            onStatusChange = { property, newStatus ->
                propertyViewModel.updatePropertyStatus(property, newStatus)
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

        propertyViewModel.properties.observe(viewLifecycleOwner) { properties ->
            propertyAdapter.updateProperties(properties)
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
            val properties = propertyViewModel.properties.value ?: emptyList()

            val locations = properties.mapNotNull { property ->
                if (property.latitude != null && property.longitude != null) {
                    LocationData(property.latitude, property.longitude, property.address)
                } else null
            }

            val intent = Intent(requireContext(), MapActivity::class.java)
            intent.putExtra("locations", ArrayList(locations))
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

