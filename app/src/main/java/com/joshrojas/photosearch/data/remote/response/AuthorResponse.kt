package com.joshrojas.photosearch.data.remote.response

import com.google.gson.annotations.SerializedName

class AuthorResponse(
    @SerializedName("email") val email: String? = null,
    @SerializedName("nickname") val nickname: String? = null
)