package com.example.cmpt362_project.addproperty

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddPropertyViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "test"
    }

    val text: LiveData<String> = _text
}