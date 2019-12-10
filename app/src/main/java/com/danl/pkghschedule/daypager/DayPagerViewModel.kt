package com.danl.pkghschedule.daypager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.danl.pkghschedule.ScheduleRepository
import com.danl.pkghschedule.model.Day

class DayPagerViewModel : ViewModel() {

    private val mDays = MediatorLiveData<List<Day>>()
    val days: LiveData<List<Day>> get() = mDays

    init {
        mDays.addSource(ScheduleRepository.days, mDays::setValue)
    }
}