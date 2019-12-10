package com.danl.pkghschedule.day

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.danl.pkghschedule.R
import com.danl.pkghschedule.ScheduleRepository
import com.danl.pkghschedule.model.Day

class DayViewModel(dayName: String?) : ViewModel() {

    private val mDay = MediatorLiveData<Day>()
    val day: LiveData<Day>
        get() = mDay

    private val mInfo = MediatorLiveData<Int>()
    val info: LiveData<Int>
        get() = mInfo

    init {
        if (dayName == null) {
            mDay.addSource(ScheduleRepository.changes) {
                if (it.subjectList.isEmpty()) {
                    mInfo.value = R.string.info_empty_change_list
                    mDay.value = null
                } else {
                    mDay.value = it
                    mInfo.value = null
                }
            }
        } else {
            mDay.addSource(ScheduleRepository.days) { dayList ->
                val day = dayList.find { it.name == dayName }
                if (day == null) {
                    mInfo.value = R.string.info_empty_subject_list
                    mDay.value = null
                } else {
                    mDay.value = day
                    mInfo.value = null
                }
            }
        }

        mInfo.addSource(ScheduleRepository.exception) {
            mInfo.value = R.string.info_error
        }
    }

    class Factory(private val dayName: String?) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return DayViewModel(dayName) as T
        }
    }
}
