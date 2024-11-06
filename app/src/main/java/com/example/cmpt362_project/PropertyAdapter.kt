package com.example.cmpt362_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PropertyAdapter(private val properties: List<Property>) : RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_property, parent, false)
        return PropertyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = properties[position]
        holder.bind(property)
    }

    override fun getItemCount() = properties.size

    class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val propertyImage: ImageView = itemView.findViewById(R.id.propertyImage)
        private val propertyAddress: TextView = itemView.findViewById(R.id.propertyAddress)
        private val propertyPrice: TextView = itemView.findViewById(R.id.propertyPrice)

        fun bind(property: Property) {
            propertyAddress.text = property.address
            propertyPrice.text = property.price
            Glide.with(itemView.context)
                .load(property.imageUrl)
                .into(propertyImage)
        }
    }
}
