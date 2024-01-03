package com.joshrojas.photosearch.data.remote.response

import com.google.gson.annotations.SerializedName

data class MediaResponse(
    @SerializedName("m") val m: String? = null
)