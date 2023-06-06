package com.dbuchin.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

class ErrorResponse (
    @field:SerializedName("error")
    val error: Boolean = false,

    @field:SerializedName("message")
    val message: String = ""
)