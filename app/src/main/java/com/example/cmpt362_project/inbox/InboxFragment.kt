package com.example.cmpt362_project.inbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cmpt362_project.R
import com.example.cmpt362_project.databinding.FragmentInboxBinding

class InboxFragment : Fragment() {

    private var _binding: FragmentInboxBinding? = null
    private val binding get() = _binding!!
    //private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val InboxViewModel =
            ViewModelProvider(this).get(InboxViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_inbox, container, false)




        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}