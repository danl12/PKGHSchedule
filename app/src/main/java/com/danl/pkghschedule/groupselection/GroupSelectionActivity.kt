package com.danl.pkghschedule.groupselection

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.danl.pkghschedule.BaseActivity
import com.danl.pkghschedule.R
import com.danl.pkghschedule.main.MainActivity
import com.danl.pkghschedule.model.Specialty
import kotlinx.android.synthetic.main.activity_group_selection.*
import kotlinx.android.synthetic.main.specialty_list_item.view.*
import java.lang.Exception

class GroupSelectionActivity : BaseActivity() {

    private val isActivityForResult
        get() = callingActivity != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_selection)

        setSupportActionBar(toolbar)
        if (isActivityForResult) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        specialtyRecyclerView.layoutManager = LinearLayoutManager(this)
        val viewModel = ViewModelProvider(this).get(GroupSelectionViewModel::class.java)
        viewModel.specialties.observe(
            this,
            Observer<List<Specialty>> {
                progressBar.visibility = View.GONE
                it.forEach { specialty -> specialty.isExpanded = false }
                specialtyRecyclerView.adapter = SpecialtyAdapter(it)
                specialtyRecyclerView.visibility = View.VISIBLE
            })
        viewModel.exception.observe(this, Observer<String> {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            progressBar.visibility = View.GONE
            infoTextView.setText(R.string.info_error)
            infoTextView.visibility = View.VISIBLE
        })
    }

    private inner class SpecialtyAdapter(val specialties: List<Specialty>) :
        RecyclerView.Adapter<SpecialtyAdapter.ViewHolder>() {

        var defaultTextColor: ColorStateList? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                layoutInflater.inflate(
                    R.layout.specialty_list_item,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount() = specialties.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val view = holder.itemView

            val specialty = specialties[position]

            view.textView.text = specialty.name

            if (defaultTextColor == null) {
                defaultTextColor = view.textView.textColors
            }

            if (specialty.isExpanded) {
                view.textView.setTextColor(
                    ContextCompat.getColor(
                        this@GroupSelectionActivity,
                        R.color.colorAccent
                    )
                )
                view.imageView.setImageResource(R.drawable.ic_expand_less)
                view.groupRecyclerView.layoutManager =
                    object : LinearLayoutManager(this@GroupSelectionActivity) {
                        override fun canScrollVertically() = false
                    }
                view.groupRecyclerView.adapter = GroupAdapter(specialty.groupList)
                view.groupRecyclerView.visibility = View.VISIBLE
                if (position != 0 && !specialties[position - 1].isExpanded) {
                    view.topSeparator.visibility = View.VISIBLE
                } else {
                    view.topSeparator.visibility = View.GONE
                }
                if (position != specialties.size - 1) {
                    view.bottomSeparator.visibility = View.VISIBLE
                } else {
                    view.bottomSeparator.visibility = View.GONE
                }
            } else {
                view.textView.setTextColor(defaultTextColor)
                view.imageView.setImageResource(R.drawable.ic_expand_more)
                view.groupRecyclerView.visibility = View.GONE
                view.topSeparator.visibility = View.GONE
                view.bottomSeparator.visibility = View.GONE
            }

            view.selectableLayout.setOnClickListener {
                specialty.isExpanded = !specialty.isExpanded

                notifyItemChanged(position)
                notifyItemChanged(position + 1)
            }
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

    private inner class GroupAdapter(val groups: List<String>) :
        RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(layoutInflater.inflate(R.layout.group_list_item, parent, false))
        }

        override fun getItemCount(): Int {
            return groups.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val group = groups[position]
            holder.itemView.textView.text = group
            holder.itemView.setOnClickListener {
                preferences.edit().putString(GROUP_NAME, group).apply()
                if (!isActivityForResult) {
                    startActivity(Intent(this@GroupSelectionActivity, MainActivity::class.java))
                }
                finish()
            }
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

}
