package com.example.storyapp.main.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.data.response.LoginResult
import com.example.storyapp.data.response.StoryResponse
import com.example.storyapp.data.store.DataStorePreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val pref: DataStorePreferences) : ViewModel() {

    private val _stories = MutableLiveData<StoryResponse>()
    val stories: LiveData<StoryResponse> = _stories

    companion object {
        const val TAG = "MapsViewModel"
    }

    fun getAllStoriesWithLocation(token: String, location: Int) {
        val client = ApiConfig().getApiService().getStoriesByLocation(token, location)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                if (response.isSuccessful) {
                    _stories.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getUser(): LiveData<LoginResult> {
        return pref.getUser().asLiveData()
    }

}