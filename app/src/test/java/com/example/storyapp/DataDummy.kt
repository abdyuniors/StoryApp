package com.example.storyapp

import com.example.storyapp.data.response.ListStory

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStory> {
        val items: MutableList<ListStory> = arrayListOf()
        for (i in 0..10) {
            val story = ListStory(
                id = "id-$i",
                name = "name $i",
                photoUrl = "https://example.com/photo_$i.jpg",
                description = "This is the description for Story $i",
                createdAt = "2023-03-02T12:34:56",
                lat = i.toDouble(),
                lon = i.toDouble(),
            )
            items.add(story)
        }
        return items
    }
}