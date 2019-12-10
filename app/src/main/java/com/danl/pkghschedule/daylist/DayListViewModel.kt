package com.danl.pkghschedule.daylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.danl.pkghschedule.R
import com.danl.pkghschedule.ScheduleRepository

class DayListViewModel : ViewModel() {

    private val mDays = MediatorLiveData<List<String>>()
    val days: LiveData<List<String>> get() = mDays

    init {
        mDays.addSource(ScheduleRepository.days) { dayList ->
            mDays.value = dayList.map { it.name }
        }
        mDays.addSource(ScheduleRepository.exception) {
            mDays.value = null
        }
    }
}
