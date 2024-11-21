package com.example.cmpt362_project.property

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PropertyViewModel : ViewModel() {
    private val allProperties = mutableListOf(
        Property(1, "422 Deer View Avenue", "CAD 3550 / month", "https://example.com/image1.jpg"),
        Property(2, "2550 Pateseabc Avenue", "CAD 2 / month", "https://example.com/image2.jpg")
    )

    private val _properties = MutableLiveData<List<Property>>()
    val properties: LiveData<List<Property>> get() = _properties

    init {
        loadProperties()
    }

    private fun loadProperties() {
        // 将完整数据加载到 _properties 中
        _properties.value = allProperties
    }

    fun deleteProperty(property: Property) {
        allProperties.remove(property)
        _properties.value = allProperties
    }

    fun searchProperties(query: String, filterStatus: String = "All") {
        val filteredList = allProperties.filter { property ->
            val matchQuery = property.address.contains(query, ignoreCase = true)
            val matchStatus = filterStatus == "All" || property.status == filterStatus
            matchQuery && matchStatus
        }
        _properties.value = filteredList
    }

    fun updatePropertyStatus(property: Property, newStatus: String) {
        val index = allProperties.indexOfFirst { it.id == property.id }
        if (index != -1) {
            allProperties[index] = allProperties[index].copy(status = newStatus)
            _properties.value = allProperties
        }
    }
}
