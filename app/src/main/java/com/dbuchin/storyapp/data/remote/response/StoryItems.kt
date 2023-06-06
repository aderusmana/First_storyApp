package com.dbuchin.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class StoryItems(
    @field:SerializedName("photoUrl")
    val photoUrl: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

)