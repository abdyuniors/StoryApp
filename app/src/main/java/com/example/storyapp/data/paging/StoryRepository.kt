package com.example.storyapp.data.paging

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.db.StoryDatabase
import com.example.storyapp.data.response.ListStory


class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    fun getStory(token: String): LiveData<PagingData<ListStory>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }
}
