package com.example.cmpt362_project.inbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cmpt362_project.ChatAdapter
import com.example.cmpt362_project.R
import com.example.cmpt362_project.databinding.FragmentInboxBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InboxFragment : Fragment() {

    private var _binding: FragmentInboxBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatAdapter: ChatAdapter

    private lateinit var  userRecyclerView: RecyclerView
    private lateinit var userList : ArrayList<user>
    private  lateinit var adapter: userAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val InboxViewModel =
            ViewModelProvider(this).get(InboxViewModel::class.java)
        _binding = FragmentInboxBinding.inflate(inflater, container, false)

        // Firebase 初始化
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()

        // 初始化用户列表
        userList = ArrayList()
        adapter = userAdapter(requireContext(), userList)

        // RecyclerView 设置
        binding.userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.userRecyclerView.adapter = adapter

        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(postSnapshot in snapshot.children){
                    val currentuser =  postSnapshot.getValue(user::class.java)
                    if(mAuth.currentUser?.uid!=currentuser?.uid){
                        userList.add(currentuser!!)
                    }

                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}