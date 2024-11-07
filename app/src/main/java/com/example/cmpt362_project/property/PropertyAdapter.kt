package com.example.cmpt362_project.property

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cmpt362_project.R

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
        private val propertyStatusSpinner: Spinner = itemView.findViewById(R.id.propertyStatusSpinner)

        fun bind(property: Property) {
            Glide.with(itemView.context)
                .load(property.imageUrl)
                .error(R.drawable.default_image)
                .into(propertyImage)

            val context = itemView.context
            val statusOptions = context.resources.getStringArray(R.array.property_status_options)

            // 自定义 ArrayAdapter 以设置选中项的文本颜色
            val adapter = object : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, statusOptions) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent) as TextView
                    view.setTextColor(ContextCompat.getColor(context, R.color.black)) // 未选中时的默认颜色为黑色
                    return view
                }

                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getDropDownView(position, convertView, parent) as TextView
                    when (statusOptions[position]) {
                        "Active" -> {
                            view.setTextColor(ContextCompat.getColor(context, R.color.green)) // 绿色文本
                        }
                        "Archived" -> {
                            view.setTextColor(ContextCompat.getColor(context, R.color.orange)) // 橙色文本
                        }
                        "Unlisted" -> {
                            view.setTextColor(ContextCompat.getColor(context, R.color.blue)) // 蓝色文本
                        }
                        else -> {
                            view.setTextColor(ContextCompat.getColor(context, R.color.black)) // 默认黑色文本
                        }
                    }
                    return view
                }
            }

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            propertyStatusSpinner.adapter = adapter

            // 初始化 Spinner 的默认选项
            val initialStatus = "Active"
            val statusIndex = statusOptions.indexOf(initialStatus)
            if (statusIndex != -1) {
                propertyStatusSpinner.setSelection(statusIndex)
            }

            // 根据选中项动态更改字体颜色
            propertyStatusSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val textView = view as? TextView
                    when (statusOptions[position]) {
                        "Active" -> textView?.setTextColor(ContextCompat.getColor(context, R.color.green))
                        "Archived" -> textView?.setTextColor(ContextCompat.getColor(context, R.color.orange))
                        "Unlisted" -> textView?.setTextColor(ContextCompat.getColor(context, R.color.blue))
                        else -> textView?.setTextColor(ContextCompat.getColor(context, R.color.black))
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            })

            // 设置点击事件跳转到 PropertyDetailsActivity
            itemView.setOnClickListener {
                val intent = Intent(context, PropertyDetailsActivity::class.java).apply {
                    putExtra("property_id", property.id)
                    putExtra("property_address", property.address)
                    putExtra("property_price", property.price)
                    putExtra("property_image_url", property.imageUrl)
                }
                context.startActivity(intent)
            }
        }
    }
}
