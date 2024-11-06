package com.example.cmpt362_project

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PropertyViewModel : ViewModel() {
    private val allProperties = listOf(
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

    fun searchProperties(query: String) {
        _properties.value = if (query.isEmpty()) {
            // 当查询为空时，显示所有数据
            allProperties
        } else {
            // 否则，过滤包含查询内容的数据
            allProperties.filter {
                it.address.contains(query, ignoreCase = true)
            }
        }
    }
}
