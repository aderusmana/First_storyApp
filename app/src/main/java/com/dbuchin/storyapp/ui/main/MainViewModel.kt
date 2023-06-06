package com.dbuchin.storyapp.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dbuchin.storyapp.data.model.UserPreference
import com.dbuchin.storyapp.data.remote.api.ApiConfig
import com.dbuchin.storyapp.data.remote.response.GetStoryResponse
import com.dbuchin.storyapp.data.remote.response.StoryItems
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: UserPreference) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _listStory = MutableLiveData<List<StoryItems>>()
    val listStory: LiveData<List<StoryItems>> = _listStory

    private val _loadingScreen = MutableLiveData<Boolean>()
    val loadingScreen: LiveData<Boolean> = _loadingScreen

    fun getStories() {
        _loadingScreen.value = true
        val cilent = ApiConfig.getApiService().getStories()
        cilent.enqueue(object : Callback<GetStoryResponse> {
            override fun onResponse(
                call: Call<GetStoryResponse>,
                response: Response<GetStoryResponse>
            ) {
                _loadingScreen.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _listStory.value = responseBody.listStory ?: emptyList()
                        Log.d(TAG, responseBody.message.toString())
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GetStoryResponse>, t: Throwable) {
                _loadingScreen.value = false
                Log.e(TAG, "onFailure2: Failed")
            }
        })
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}