package com.example.storyapp.auth.login

import android.util.Log
import androidx.lifecycle.*
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.data.response.LoginResponse
import com.example.storyapp.data.store.DataStorePreferences
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: DataStorePreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<LoginResponse>()
    val loginResult: LiveData<LoginResponse> = _loginResult

    val error = MutableLiveData("")
    val message = MutableLiveData("")

    companion object {
        const val TAG = "LoginViewModel"
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig().getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                when (response.code()) {
                    200 -> {
                        _loginResult.postValue(response.body())
                        message.postValue("200")
                    }
                    400 -> error.postValue("400")
                    401 -> error.postValue("401")
                    else -> error.postValue("ERROR ${response.code()} : ${response.message()}")
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }

        })

    }

    fun saveUser(userId: String, name: String, token: String) {
        viewModelScope.launch {
            pref.saveUser(userId, name, token)
        }
    }

}