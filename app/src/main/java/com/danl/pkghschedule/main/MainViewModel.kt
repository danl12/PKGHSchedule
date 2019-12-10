package com.danl.pkghschedule.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.danl.pkghschedule.ScheduleRepository

class MainViewModel : ViewModel() {

    private val mException = MediatorLiveData<String>()
    val exception: LiveData<String>
        get() = mException

    init {
        mException.addSource(ScheduleRepository.exception, mException::setValue)

    }

    fun loadSchedule(groupName: String) {
        ScheduleRepository.loadSchedule(groupName)
    }
}