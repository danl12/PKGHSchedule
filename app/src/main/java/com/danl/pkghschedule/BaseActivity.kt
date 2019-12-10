package com.danl.pkghschedule

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    protected companion object {
        const val DARK_THEME = "dark_theme"
        const val GROUP_NAME = "group_name"
    }

    protected var mDarkTheme = false

    val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDarkTheme = preferences.getBoolean(DARK_THEME, false)
        if (mDarkTheme) {
            setTheme(R.style.AppTheme_Dark)
        }
    }

    override fun onResume() {
        super.onResume()

        if (mDarkTheme != preferences.getBoolean(DARK_THEME, false)) {
            recreate()
        }
    }
}