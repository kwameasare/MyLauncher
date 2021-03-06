package io.github.micopiira.mylauncher

import android.app.Activity
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.GridView
import android.widget.SearchView

import butterknife.BindString
import butterknife.BindView
import butterknife.ButterKnife

class HomeActivity : Activity(), SharedPreferences.OnSharedPreferenceChangeListener {

    @BindView(R.id.apps_list) lateinit var appList: GridView
    @BindView(R.id.searchview) lateinit var searchView: SearchView
    @BindString(R.string.app_name) lateinit var appName: String

    private val isPortrait: Boolean
        get() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apps_list)
        ButterKnife.bind(this)

        val apps = AppRepository(this, appName).findAll()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val appsAdapter = AppsAdapter(this, apps)
        appList.numColumns = Integer.parseInt(sharedPreferences.getString(if (isPortrait) "grid_columns_portrait" else "grid_columns_landscape", "4"))
        appList.adapter = appsAdapter
        appList.setOnItemClickListener { _, _, position, _ -> startActivity(appsAdapter.filteredApps[position].intent) }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                appsAdapter.filter.filter(p0)
                return false
            }
        })

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        recreate()
    }
}
