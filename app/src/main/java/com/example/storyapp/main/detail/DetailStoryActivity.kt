package com.example.storyapp.main.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.storyapp.data.response.ListStory
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.utils.withDateFormat

@Suppress("DEPRECATION")
class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Story"

        val data = intent.getParcelableExtra<ListStory>(EXTRA_DATA)
        binding.apply {
            tvName.text = data?.name
            imgUploaded.load(data?.photoUrl)
            tvDescription.text = data?.description
            tvCreatedAt.text =
                StringBuilder().append("Posted at ").append(data?.createdAt?.withDateFormat())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}