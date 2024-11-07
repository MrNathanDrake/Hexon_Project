package com.example.cmpt362_project.addproperty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cmpt362_project.databinding.FragmentAddpropertyBinding


class AddPropertyFragment : Fragment() {

    private var _binding: FragmentAddpropertyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val addPropertyViewModel =
            ViewModelProvider(this).get(AddPropertyViewModel::class.java)

        _binding = FragmentAddpropertyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textAddproperty
        addPropertyViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}