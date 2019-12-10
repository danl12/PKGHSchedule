package com.danl.pkghschedule.daypager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.danl.pkghschedule.BaseActivity
import com.danl.pkghschedule.R
import com.danl.pkghschedule.day.DayFragment
import com.danl.pkghschedule.model.Day
import kotlinx.android.synthetic.main.activity_day_pager.*

class DayPagerActivity : BaseActivity() {

    companion object {
        private const val EXTRA_DAY_NAME = "day_name"

        fun newIntent(packageContext: Context, dayName: String): Intent {
            val intent = Intent(packageContext, DayPagerActivity::class.java)
            intent.putExtra(EXTRA_DAY_NAME, dayName)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_pager)

        setSupportActionBar(toolbar)
        title = preferences.getString(GROUP_NAME, "")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val viewModel = ViewModelProvider(this).get(DayPagerViewModel::class.java)
        viewModel.days.observe(this, Observer<List<Day>> { datList ->
            viewPager.adapter = object : FragmentStatePagerAdapter(
                supportFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ) {
                override fun getItem(position: Int): Fragment =
                    DayFragment.newInstance(datList[position].name)

                override fun getCount(): Int = datList.size
            }
            val dayName = intent.getStringExtra(EXTRA_DAY_NAME)
            viewPager.currentItem = datList.indexOfFirst { it.name == dayName }
        })
    }
}
