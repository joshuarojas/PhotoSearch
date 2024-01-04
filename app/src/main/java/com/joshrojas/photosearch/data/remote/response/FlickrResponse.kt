package com.joshrojas.photosearch.data.remote.response

import com.google.gson.annotations.SerializedName
import java.util.Date

data class FlickrResponse(
    @SerializedName("title") val title: String? = null,
    @SerializedName("link") val link: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("modified") val modified: Date? = null,
    @SerializedName("generator") val generator: String? = null,
    @SerializedName("items") val items: ArrayList<ItemResponse> = arrayListOf()
)