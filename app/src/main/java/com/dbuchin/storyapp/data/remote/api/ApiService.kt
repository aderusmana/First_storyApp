package com.dbuchin.storyapp.data.remote.api

import com.dbuchin.storyapp.data.remote.payload.PayloadLogin
import com.dbuchin.storyapp.data.remote.payload.PayloadRegister
import com.dbuchin.storyapp.data.remote.response.AddStoryResponse
import com.dbuchin.storyapp.data.remote.response.GetDetailStoryResponse
import com.dbuchin.storyapp.data.remote.response.GetStoryResponse
import com.dbuchin.storyapp.data.remote.response.LoginResponse
import com.dbuchin.storyapp.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @Headers("No-Authentication: true")
    @POST("/v1/login")
    fun login(
        @Body payload: PayloadLogin
    ): Call<LoginResponse>

    @Headers("No-Authentication: true")
    @POST("/v1/register")
    fun register(
        @Body payload: PayloadRegister
    ): Call<RegisterResponse>

    @GET("/v1/stories")
    fun getStories(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int = 0
    ): Call<GetStoryResponse>

    @GET("/v1/stories/{userId}")
    fun getDetailStory(
        @Path("userId") userId: String
    ): Call<GetDetailStoryResponse>

    @Multipart
    @POST("/v1/stories")
    fun postStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<AddStoryResponse>
}