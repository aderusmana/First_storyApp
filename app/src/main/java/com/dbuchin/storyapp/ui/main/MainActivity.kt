package com.dbuchin.storyapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.dbuchin.storyapp.R
import com.dbuchin.storyapp.ViewModelFactory
import com.dbuchin.storyapp.data.model.UserPreference
import com.dbuchin.storyapp.data.remote.response.StoryItems
import com.dbuchin.storyapp.databinding.ActivityMainBinding
import com.dbuchin.storyapp.ui.adapter.StoryAdapter
import com.dbuchin.storyapp.ui.addstory.AddStoryActivity
import com.dbuchin.storyapp.ui.login.LoginActivity

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, MainActivity::class.java)
            starter.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(starter)
        }
    }

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory(UserPreference.getInstance(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.root.setOnRefreshListener {
            setupView()
        }
        binding.fabAddStory.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
        }
        setupView()
        setupViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> showLogoutDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("Are you sure you want to Logout?")
            setPositiveButton("Yes") { _, _ ->
                logout()
            }
            setNegativeButton("No", null)
            create()
            show()
        }
    }

    private fun logout() {
        mainViewModel.logout()
        LoginActivity.start(this)
        finish()
    }

    private fun setupView() {
        binding.root.isRefreshing = false
        mainViewModel.getStories()
    }

    private fun setupViewModel() {
        mainViewModel.listStory.observe(this) {
            setRecycleView(it)
        }
        mainViewModel.loadingScreen.observe(this) {
            showLoading(it)
        }
    }

    private fun showLoading(value: Boolean) {
        binding.pbLoadingScreen.isVisible = value
        binding.rvStory.isVisible = !value
    }

    private fun setRecycleView(list: List<StoryItems>) {
        with(binding) {
            val manager = LinearLayoutManager(this@MainActivity)
            rvStory.apply {
                adapter = StoryAdapter(list)
                layoutManager = manager
            }
        }
    }
}