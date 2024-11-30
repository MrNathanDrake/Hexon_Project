package com.example.cmpt362_project.property

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cmpt362_project.R
import com.google.android.gms.common.internal.Objects.ToStringHelper

class PropertyAdapter(
    private var properties: List<Property>,
    private val onDeleteClick: (Property) -> Unit, // Delete callback
    private val onStatusChange: (Property, String) -> Unit,  // status change callback
    private val onViewClick: (Property) -> Unit // view callback
) : RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>() {

    private val initializedSpinners = mutableSetOf<String>()

    fun updateProperties(newProperties: List<Property>) {
        properties = newProperties
        initializedSpinners.clear() // Reset spinner tracking
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_property, parent, false)
        return PropertyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = properties[position]
        holder.bind(property) // Bind the property to the view holder
    }

    override fun getItemCount() = properties.size

    // Inner class to access 'onDeleteClick' directly
    inner class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val propertyAddress: TextView = itemView.findViewById(R.id.propertyAddress)
        private val propertyPrice: TextView = itemView.findViewById(R.id.propertyPrice)
        private val propertyImage: ImageView = itemView.findViewById(R.id.propertyImage)
        private val propertyStatusSpinner: Spinner = itemView.findViewById(R.id.propertyStatusSpinner)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
        private val viewButton: ImageButton = itemView.findViewById(R.id.viewButton)

        fun bind(property: Property) {
            Glide.with(itemView.context)
                .load(property.imageUrl)
                .error(R.drawable.default_image)
                .into(propertyImage)

            val formattedPrice = "CAD ${property.rent} / month"

            propertyAddress.text = property.address
            propertyPrice.text = formattedPrice

            // set view button click listener
            viewButton.setOnClickListener {
                onViewClick(property)
            }

            // Set delete button click listener
            deleteButton.setOnClickListener {
                onDeleteClick(property) // Access 'onDeleteClick' directly
            }

            val context = itemView.context
            val statusOptions = context.resources.getStringArray(R.array.property_status_options)

            // Custom ArrayAdapter for setting selected text color
            val adapter = object : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, statusOptions) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent) as TextView
                    view.setTextColor(
                        when (statusOptions[position]) {
                            "Active" -> ContextCompat.getColor(context, R.color.green)
                            "Archived" -> ContextCompat.getColor(context, R.color.orange)
                            "Unlisted" -> ContextCompat.getColor(context, R.color.blue)
                            else -> ContextCompat.getColor(context, R.color.black)
                        }
                    )
                    return view
                }

                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getDropDownView(position, convertView, parent) as TextView
                    view.setTextColor(
                        when (statusOptions[position]) {
                            "Active" -> ContextCompat.getColor(context, R.color.green)
                            "Archived" -> ContextCompat.getColor(context, R.color.orange)
                            "Unlisted" -> ContextCompat.getColor(context, R.color.blue)
                            else -> ContextCompat.getColor(context, R.color.black)
                        }
                    )
                    return view
                }
            }

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            propertyStatusSpinner.adapter = adapter

            // Initialize Spinner selection based on status
            val currentStatusIndex = statusOptions.indexOf(property.status)
            if (currentStatusIndex != -1) {
                propertyStatusSpinner.setSelection(currentStatusIndex)
            }


            // Dynamically change text color based on selected item
            propertyStatusSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (initializedSpinners.contains(property.id)) {
                        val selectedStatus = statusOptions[position]

                        (view as? TextView)?.setTextColor(
                            when (selectedStatus) {
                                "Active" -> ContextCompat.getColor(context, R.color.green)
                                "Archived" -> ContextCompat.getColor(context, R.color.orange)
                                "Unlisted" -> ContextCompat.getColor(context, R.color.blue)
                                else -> ContextCompat.getColor(context, R.color.black)
                            }
                        )

                        Toast.makeText(context, "Changed the status to: $selectedStatus", Toast.LENGTH_SHORT).show()

                        // update the status if it has changed
                        if (selectedStatus != property.status) {
                            onStatusChange(property, selectedStatus)
                        }

                    } else {
                        property.id?.let { initializedSpinners.add(it) }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            })

            // Set item click listener to open PropertyDetailsActivity
            itemView.setOnClickListener {
                val intent = Intent(context, PropertyDetailsActivity::class.java).apply {
                    putExtra("property_id", property.id)
                    putExtra("property_address", property.address)
                    putExtra("property_image_url", property.imageUrl)
                }
                context.startActivity(intent)
            }


            // Add click listeners for the Craigslist and Facebook ImageViews
            val craigslistImageView: ImageView = itemView.findViewById(R.id.craigslist)
            val facebookImageView: ImageView = itemView.findViewById(R.id.facebook)

            craigslistImageView.setOnClickListener {
                val craigslistIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vancouver.craigslist.org/"))
                context.startActivity(craigslistIntent)
            }

            facebookImageView.setOnClickListener {
                val facebookIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"))
                context.startActivity(facebookIntent)
            }
        }
    }
}