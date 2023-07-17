package com.example.storyapp.main.add

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.data.response.LoginResult
import com.example.storyapp.data.response.StoryUploadResponse
import com.example.storyapp.data.store.DataStorePreferences
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryViewModel(private val pref: DataStorePreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val responseLiveData = MutableLiveData<StoryUploadResponse?>()

    companion object {
        const val TAG = "AddStoryViewModel"
    }

    fun postStory(
        token: String,
        imageFile: File,
        desc: String
    ): MutableLiveData<StoryUploadResponse?> {
        val description = desc.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val image = MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)
        val client = ApiConfig().getApiService().uploadStory(token, image, description)
        _isLoading.value = true
        client.enqueue(object : Callback<StoryUploadResponse> {
            override fun onResponse(
                call: Call<StoryUploadResponse>,
                response: Response<StoryUploadResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { storyUploadResponse ->
                        responseLiveData.value = storyUploadResponse
                    }
                } else {
                    Log.e(TAG, "Error uploading story: ${response.code()}")
                    responseLiveData.value = null
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<StoryUploadResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "Failed to upload story", t)
                responseLiveData.value = null
            }
        })
        return responseLiveData
    }

    fun getUser(): LiveData<LoginResult> {
        return pref.getUser().asLiveData()
    }
}