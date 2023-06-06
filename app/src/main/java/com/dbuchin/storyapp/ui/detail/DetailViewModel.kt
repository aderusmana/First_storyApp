package com.dbuchin.storyapp.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dbuchin.storyapp.data.remote.api.ApiConfig
import com.dbuchin.storyapp.data.remote.response.GetDetailStoryResponse
import com.dbuchin.storyapp.data.remote.response.GetStoryResponse
import com.dbuchin.storyapp.data.remote.response.StoryItems
import com.dbuchin.storyapp.util.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {

    companion object {
        private const val TAG = "DetailStoryViewModel"
    }

    private val _detailStory = MutableLiveData<StoryItems>()
    val detailStory: LiveData<StoryItems> = _detailStory

    private val _loadingScreen = MutableLiveData<Boolean>()
    val loadingScreen: LiveData<Boolean> = _loadingScreen

    private val _snackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = _snackBarText

    fun getDetailStory(userId: String) {
        _loadingScreen.value = true

        val cilent = ApiConfig.getApiService().getDetailStory(userId)
        cilent.enqueue(object : Callback<GetDetailStoryResponse> {
            override fun onResponse(
                call: Call<GetDetailStoryResponse>,
                response: Response<GetDetailStoryResponse>
            ) {
                _loadingScreen.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _detailStory.value = responseBody.story ?: StoryItems()
                        Log.d(TAG, responseBody.message.toString())
                    }
                } else {
                    _snackBarText.value = Event(response.message())
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GetDetailStoryResponse>, t: Throwable) {
                _loadingScreen.value = false
                _snackBarText.value = Event("onFailure: Gagal, ${t.message ?: ""}")
                Log.e(TAG, "onFailure: Gagal")
            }
        })
    }
}