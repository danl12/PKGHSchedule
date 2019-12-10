package com.danl.pkghschedule.groupselection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.danl.pkghschedule.ScheduleRepository
import com.danl.pkghschedule.model.Specialty

class GroupSelectionViewModel: ViewModel() {

    private val mSpecialties = MediatorLiveData<List<Specialty>>()
    val specialties: LiveData<List<Specialty>>
        get() = mSpecialties

    private val mException = MediatorLiveData<String>()
    val exception: LiveData<String>
        get() = mException

    init {
        mSpecialties.addSource(ScheduleRepository.specialties, mSpecialties::setValue)
        mException.addSource(ScheduleRepository.exception, mException::setValue)
    }
}