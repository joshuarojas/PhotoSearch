package com.joshrojas.photosearch.data.remote.response

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ItemResponse(
    val id: Long = System.nanoTime(),
    var isFocused: Boolean =  false,
    @SerializedName("title") val title: String? = null,
    @SerializedName("link") val link: String? = null,
    @SerializedName("media") val media: MediaResponse? = null,
    @SerializedName("date_taken") val dateTaken: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("published") val published: Date? = null,
    @SerializedName("author") val author: AuthorResponse? = null,
    @SerializedName("author_id") val authorId: String? = null,
    @SerializedName("tags") val tags: String? = null
)