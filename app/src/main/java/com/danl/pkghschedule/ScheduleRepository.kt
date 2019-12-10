package com.danl.pkghschedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.danl.pkghschedule.model.Day
import com.danl.pkghschedule.model.Specialty
import com.danl.pkghschedule.model.Subject
import okhttp3.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.IOException
import java.util.*

object ScheduleRepository {

    private val mException = MutableLiveData<String>()
    val exception: LiveData<String>
        get() = mException

    private val mDays = MutableLiveData<List<Day>>()
    val days: LiveData<List<Day>>
        get() = mDays

    private val mChanges = MutableLiveData<Day>()
    val changes: LiveData<Day>
        get() = mChanges

    private val mSpecialties = MutableLiveData<List<Specialty>>()
    val specialties: LiveData<List<Specialty>>
        get() = mSpecialties

    private var document: Document? = null

    init {
        loadSpecialties()
    }

    private fun loadSpecialties() {
        if (document != null) {
            parseSpecialties()
            return
        }
        val request = Request.Builder()
            .url("https://pkgh.edu.ru/obuchenie/shedule-of-classes.html")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mException.postValue(e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    document = Jsoup.parse(response.body?.string() ?: return)
                    parseSpecialties()
                }
            }
        })
    }

    fun loadSchedule(groupName: String) {
        if (document != null) {
            parseSchedule(groupName)
            return
        }
        val request = Request.Builder()
            .url("https://pkgh.edu.ru/obuchenie/shedule-of-classes.html")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mException.postValue(e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    document = Jsoup.parse(response.body?.string() ?: return)
                    parseSchedule(groupName)
                }
            }
        })
    }

    private fun parseSpecialties() {
        val specialtyList = mutableListOf<Specialty>()

        var lastSpecialtyName: String? = null
        var lastGroupList: MutableList<String>? = null
        try {
            for (element in document!!.selectFirst("div.shedule-container").children()) {
                if (element.tagName() == "h4") {
                    if (lastSpecialtyName != null && lastGroupList != null) {
                        specialtyList.add(Specialty(lastSpecialtyName, lastGroupList))
                    }
                    lastSpecialtyName = element.text()
                    lastGroupList = mutableListOf()
                } else if (element.tagName() == "div") {
                    val groupElements = element.select("div[class=column one-fourth]")
                    for (groupElement in groupElements) {
                        groupElement.selectFirst("h4")?.let {
                            lastGroupList?.add(it.text())
                        }
                    }
                }
            }
            if (lastSpecialtyName != null && lastGroupList != null) {
                specialtyList.add(Specialty(lastSpecialtyName, lastGroupList))
            }
            mSpecialties.postValue(specialtyList)
        } catch (e: Exception) {
            mException.postValue("Не удалось загрузить расписание.")
        }
    }

    private fun parseSchedule(groupName: String) {
        val groupElements = document!!.select("div[class=column one-fourth]")
        val dayElements = groupElements.find {
            it.selectFirst("h4")?.text() == groupName
        }?.select("table.shedule")
        val dayList = dayElements?.map {
            parseDay(it)
        }?.filter { it.subjectList.isNotEmpty() } ?: emptyList()
        mDays.postValue(dayList)

        val changesDate =
            document!!.selectFirst("div[class=custom otherpadding changes] h4")?.text()
                ?: ""
        val changeElements =
            document!!.select("div[class=custom otherpadding changes] tbody tr")
        val changeList = changeElements.asSequence()
            .filter { it.select("td[class=highlightable group]").text() == groupName }
            .map {
                val num = it.selectFirst("td[class^=pnum]")?.text() ?: ""

                val changeElement = it.select("td[class=highlightable onepair]").last()
                val name = changeElement?.selectFirst("p.pname")?.text() ?: ""
                val teacher = changeElement?.selectFirst("p.pteacher")?.text() ?: ""

                Subject(num, name, teacher)
            }
            .toList()
        mChanges.postValue(Day(changesDate, changeList))
    }

    private fun parseDay(dayElement: Element): Day {
        val dayName = dayElement.selectFirst("p.groupname")?.text() ?: ""

        val subjectElements = dayElement.select("tr.pair")
        val subjectList = subjectElements.map {
            val num = it.selectFirst("td[class^=pnum]")?.text() ?: ""
            var name = ""
            var teacher = ""

            if (byDenominator()) {
                name = it.selectFirst("p.paltname")?.text() ?: ""
                teacher = it.selectFirst("p.paltteacher")?.text() ?: ""
            }

            if (name.isEmpty() || teacher.isEmpty()) {
                name = it.selectFirst("p.pname")?.text() ?: ""
                teacher = it.selectFirst("p.pteacher")?.text() ?: ""
            }

            Subject(num, name, teacher)
        }.filter { it.name.isNotEmpty() || it.teacher.isNotEmpty() }

        return Day(dayName, subjectList)
    }

    private fun byDenominator(): Boolean =
        (Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) + 1) % 2 == 0
}