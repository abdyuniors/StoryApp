package com.example.storyapp.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.paging.StoryRepository
import com.example.storyapp.data.response.ListStory
import com.example.storyapp.data.response.LoginResult
import com.example.storyapp.data.store.DataStorePreferences
import kotlinx.coroutines.launch

class MainViewModel(
    private val pref: DataStorePreferences,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getStories(token: String): LiveData<PagingData<ListStory>> =
        storyRepository.getStory(token).cachedIn(viewModelScope)

    fun getUser(): LiveData<LoginResult> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

}