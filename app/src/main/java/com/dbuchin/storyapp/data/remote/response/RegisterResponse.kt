package com.dbuchin.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

class RegisterResponse (
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
    )