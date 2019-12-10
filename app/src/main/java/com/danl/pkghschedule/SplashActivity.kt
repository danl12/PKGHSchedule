package com.danl.pkghschedule

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.preference.PreferenceManager
import com.danl.pkghschedule.groupselection.GroupSelectionActivity
import com.danl.pkghschedule.main.MainActivity

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val c = if (PreferenceManager.getDefaultSharedPreferences(this).contains(GROUP_NAME)) {
            MainActivity::class.java
        } else {
            GroupSelectionActivity::class.java
        }
        startActivity(Intent(this, c))
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return false
    }
}