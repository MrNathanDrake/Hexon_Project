package com.example.cmpt362_project

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cmpt362_project.databinding.ActivityInboxBinding

class InboxActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInboxBinding
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbox)

        // 初始化 RecyclerView
        chatAdapter = ChatAdapter()
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = chatAdapter

//        loadChats()
//
//        // 搜索框监听
//        binding.searchChatEditText.addTextChangedListener { query ->
//            searchChats(query.toString())
//        }
    }

    private fun loadChats() {
        // 模拟加载聊天记录
        val chatList = listOf<Chat>() // 可以替换为真实数据
        chatAdapter.submitList(chatList)

        // 根据是否有聊天记录显示不同的 UI
        binding.noChatTextView.visibility = if (chatList.isEmpty()) View.VISIBLE else View.GONE
    }

//    private fun searchChats(query: String) {
//        // 实现搜索逻辑
//        chatAdapter.filter(query)
//    }

}