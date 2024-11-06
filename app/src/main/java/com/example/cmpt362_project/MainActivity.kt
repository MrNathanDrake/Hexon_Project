package com.example.cmpt362_project

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cmpt362_project.property.PropertyAdapter
import com.example.cmpt362_project.property.PropertyViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var propertyAdapter: PropertyAdapter
    private val propertyViewModel: PropertyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.propertyRecyclerView)
        val searchEditText: EditText = findViewById(R.id.searchEditText)

        propertyAdapter = PropertyAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = propertyAdapter

        // 观察 ViewModel 中的 properties 数据
        propertyViewModel.properties.observe(this, Observer { properties ->
            propertyAdapter = PropertyAdapter(properties)
            recyclerView.adapter = propertyAdapter
        })

        // 实现搜索功能
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                propertyViewModel.searchProperties(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        // 获取每个导航项的布局
        val navDashboardLayout = findViewById<LinearLayout>(R.id.navDashboardLayout)
        val navCartLayout = findViewById<LinearLayout>(R.id.navAddLayout)
        val navOrderLayout = findViewById<LinearLayout>(R.id.navInboxLayout)
        val navProfileLayout = findViewById<LinearLayout>(R.id.navProfileLayout)


        // 设置点击事件监听器
        navDashboardLayout.setOnClickListener {
            // 跳转到 DashboardActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        navCartLayout.setOnClickListener {
            // 跳转到 AddPropertyActivity
            val intent = Intent(this, AddPropertyActivity::class.java)
            startActivity(intent)
        }

        navOrderLayout.setOnClickListener {
            // 跳转到 InboxActivity
            val intent = Intent(this, InboxActivity::class.java)
            startActivity(intent)
        }

        navProfileLayout.setOnClickListener {
            // 跳转到 ProfileActivity
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}