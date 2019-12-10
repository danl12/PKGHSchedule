package com.danl.pkghschedule.daylist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.danl.pkghschedule.R
import kotlinx.android.synthetic.main.fragment_day_list.*
import kotlinx.android.synthetic.main.list_item.view.*

class DayListFragment : Fragment() {

    private var mListener: Listener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_day_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        specialtyRecyclerView.layoutManager = LinearLayoutManager(context)

        val viewModel = ViewModelProvider(this).get(DayListViewModel::class.java)
        viewModel.days.observe(viewLifecycleOwner, Observer<List<String>> {
            progressBar.visibility = View.GONE
            if (it == null) {
                infoTextView.setText(R.string.info_error)
                infoTextView.visibility = View.VISIBLE
            } else {
                specialtyRecyclerView.swapAdapter(DayAdapter(it), false)
                specialtyRecyclerView.visibility = View.VISIBLE
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as Listener
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    private inner class DayAdapter(private val mDays: List<String>) :
        RecyclerView.Adapter<DayAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = layoutInflater.inflate(R.layout.list_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = mDays.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val day = mDays[position]

            val view = holder.itemView

            view.textView.text = day
            view.setOnClickListener {
                mListener?.onSelected(day)
            }
        }

        private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }

    interface Listener {
        fun onSelected(day: String)
    }
}
