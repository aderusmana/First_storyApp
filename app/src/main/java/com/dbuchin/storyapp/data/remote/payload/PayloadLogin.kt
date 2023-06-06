package com.dbuchin.storyapp.data.remote.payload

import com.google.gson.annotations.SerializedName

data class PayloadLogin(
    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("password")
    val password: String
)