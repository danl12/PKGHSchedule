package com.danl.pkghschedule.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.danl.pkghschedule.BaseActivity
import com.danl.pkghschedule.CardActivity
import com.danl.pkghschedule.groupselection.GroupSelectionActivity
import com.danl.pkghschedule.R
import com.danl.pkghschedule.day.DayFragment
import com.danl.pkghschedule.daylist.DayListFragment
import com.danl.pkghschedule.daypager.DayPagerActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseActivity(), DayListFragment.Listener {

    private companion object {
        private const val GROUP_SELECTION_ACTIVITY = 0
        private const val CARD_ACTIVITY = 1
    }

    private lateinit var mViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val titles = resources.getStringArray(R.array.page_titles)
        val daysOfWeek = resources.getStringArray(R.array.days_of_week)

        viewPager.adapter = object :
            FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

            override fun getItem(position: Int) = when (position) {
                0 -> DayFragment()
                3 -> DayListFragment()
                else -> {
                    val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + position - 2
                    DayFragment.newInstance(daysOfWeek[if (dayOfWeek < 7) dayOfWeek else 0])
                }
            }

            override fun getCount(): Int = 4

            override fun getPageTitle(position: Int): CharSequence? = titles[position]
        }
        tabLayout.setupWithViewPager(viewPager)
        viewPager.currentItem = 1
        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        if (savedInstanceState == null) {
            mViewModel.exception.observe(this, Observer<String> {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            })
        }
        updateGroup()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menu.findItem(R.id.dark_theme).isChecked = mDarkTheme
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.dark_theme) {
            preferences.edit().putBoolean(DARK_THEME, !item.isChecked).apply()
            recreate()
            return true
        } else if (item.itemId == R.id.group_name) {
            startActivityForResult(Intent(this, GroupSelectionActivity::class.java), GROUP_SELECTION_ACTIVITY)
            return true
        } else if (item.itemId == R.id.read_card) {
            startActivityForResult(Intent(this, CardActivity::class.java), CARD_ACTIVITY)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSelected(day: String) {
        startActivity(DayPagerActivity.newIntent(this, day))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GROUP_SELECTION_ACTIVITY) {
            updateGroup()
        }
    }

    private fun updateGroup() {
        preferences.getString(GROUP_NAME, null)!!.let {
            title = it
            mViewModel.loadSchedule(it)
        }
    }
}
