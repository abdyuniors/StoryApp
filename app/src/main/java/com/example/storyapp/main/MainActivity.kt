package com.example.storyapp.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.auth.WelcomeActivity
import com.example.storyapp.data.store.DataStorePreferences
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.main.add.AddStoryActivity
import com.example.storyapp.main.maps.MapsActivity
import com.example.storyapp.utils.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "User")

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = StoryAdapter()
        binding.apply {
            rvStory.layoutManager = LinearLayoutManager(this@MainActivity)
            rvStory.setHasFixedSize(true)
            rvStory.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter { adapter.retry() }
            )
        }

        viewModel = ViewModelProvider(
            this, ViewModelFactory(DataStorePreferences.getInstance(dataStore), this)
        )[MainViewModel::class.java]

        showLoading(true)
        viewModel.getUser().observe(this) { user ->
            if (user.userId.isNotEmpty()) {
                binding.tvUser.text = getString(R.string.greeting, user.name)
                viewModel.getStories("Bearer ${user.token}").observe(this) {
                    adapter.submitData(lifecycle, it)
                    showLoading(false)
                }
            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionLogout -> {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.logout))
                    setMessage(getString(R.string.logout_message_dialog))
                    setPositiveButton(getString(R.string.yes)) { _, _ ->
                        viewModel.logout()
                    }
                    setNegativeButton(getString(R.string.no)) { dialog, _ ->
                        dialog.cancel()
                    }
                }.show()
                true
            }
            R.id.actionMap -> {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.rvStory.adapter = null
    }
}