package com.danl.pkghschedule.day

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.danl.pkghschedule.R
import com.danl.pkghschedule.model.Day
import com.danl.pkghschedule.model.Subject
import kotlinx.android.synthetic.main.fragment_day.*
import kotlinx.android.synthetic.main.subject_list_item.view.*

class DayFragment : Fragment() {

    companion object {
        private const val DAY_NAME = "day_name"

        private val timeMap = mapOf(
            "1" to "09:10 - 10:40",
            "2" to "10:55 - 12:25",
            "3" to "13:05 - 14:35",
            "4" to "14:50 - 16:20",
            "5" to "16:30 - 18:00",
            "6" to "18:15 - 19:45"
        )
        private val saturdayTimeMap = mapOf(
            "1" to "09:10 - 10:40",
            "2" to "10:50 - 12:20",
            "3" to "12:50 - 14:20",
            "4" to "14:30 - 16:00",
            "5" to "16:10 - 17:40",
            "6" to "17:50 - 19:20"
        )

        fun newInstance(day: String): DayFragment {
            val subjectListFragment = DayFragment()

            val bundle = Bundle()
            bundle.putString(DAY_NAME, day)
            subjectListFragment.arguments = bundle

            return subjectListFragment
        }
    }

    private var mDayName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDayName = arguments?.getString(DAY_NAME)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_day, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        specialtyRecyclerView.layoutManager =
            if (context!!.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) LinearLayoutManager(
                context
            ) else StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        specialtyRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                separator.visibility =
                    if (recyclerView.canScrollVertically(-1)) View.VISIBLE else View.GONE
            }
        })

        val viewModel =
            ViewModelProvider(this, DayViewModel.Factory(mDayName)).get(
                DayViewModel::class.java
            )
        viewModel.day.observe(viewLifecycleOwner, Observer<Day> {
            it?.let {
                nameTextView.text = it.name
                specialtyRecyclerView.swapAdapter(SubjectAdapter(it.subjectList), false)
                progressBar.visibility = View.GONE
                infoTextView.visibility = View.GONE
                dayLayout.visibility = View.VISIBLE
            }
        })
        viewModel.info.observe(viewLifecycleOwner, Observer<Int> {
            it?.let {
                infoTextView.setText(it)
                progressBar.visibility = View.GONE
                dayLayout.visibility = View.GONE
                infoTextView.visibility = View.VISIBLE
            }
        })
    }

    private inner class SubjectAdapter(private val mSubjects: List<Subject>) :
        RecyclerView.Adapter<SubjectAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = layoutInflater.inflate(R.layout.subject_list_item, parent, false)
            return ViewHolder(
                view
            )
        }

        override fun getItemCount(): Int = mSubjects.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val subject = mSubjects[position]

            val view = holder.itemView

            view.numTextView.text = subject.num
            view.nameTextView.text = subject.name
            view.teacherTextView.text = subject.teacher
            view.timeTextView.text =
                when (mDayName) {
                    null -> {
                        ""
                    }
                    "Суббота" -> saturdayTimeMap[subject.num]
                    else -> timeMap[subject.num]
                } ?: ""
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }
}
