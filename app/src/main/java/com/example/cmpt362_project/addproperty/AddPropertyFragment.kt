package com.example.cmpt362_project.addproperty

import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cmpt362_project.R
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
        val view = binding.root

        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.propertytoolbar)
            supportActionBar?.apply {
                title = ""
                setDisplayHomeAsUpEnabled(true)
            }
        }

        binding.propertytoolbar.setNavigationOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.nextStepButton.setOnClickListener {
            val intent = Intent(requireContext(), AddPropertyDescription::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}