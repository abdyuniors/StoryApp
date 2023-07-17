package com.example.storyapp.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.storyapp.data.response.ListStory
import com.example.storyapp.databinding.ItemStoryBinding
import com.example.storyapp.main.detail.DetailStoryActivity
import com.example.storyapp.utils.withDateFormat

class StoryAdapter : PagingDataAdapter<ListStory, StoryAdapter.StoryViewHolder>(diffCallback) {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<ListStory>() {
            override fun areItemsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStory) {
            binding.apply {
                tvName.text = story.name
                imgPhoto.load(story.photoUrl) {
                    crossfade(true)
                    crossfade(1000)
                }
                tvDescription.text = story.description
                tvCreatedAt.text = story.createdAt?.withDateFormat()

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                    intent.putExtra(DetailStoryActivity.EXTRA_DATA, story)
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as MainActivity,
                            androidx.core.util.Pair(binding.imgPhoto, "photo"),
                            androidx.core.util.Pair(binding.tvName, "name"),
                            androidx.core.util.Pair(binding.tvDescription, "description"),
                            androidx.core.util.Pair(binding.tvCreatedAt, "createdAt")
                        )
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }
}