package com.raihan.projectlaundry.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SlideshowViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Aplikasi Laundry" +
                "\nVersi 1.0" +
                "\nDibuat oleh kelompok 8" +
                "\nPoliteknik Negeri Padang"
    }
    val text: LiveData<String> = _text
}