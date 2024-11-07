package com.example.cmpt362_project.inbox

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InboxViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "testtest"
    }
    val text: LiveData<String> = _text
}