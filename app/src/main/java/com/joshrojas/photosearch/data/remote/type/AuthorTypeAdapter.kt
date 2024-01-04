package com.joshrojas.photosearch.data.remote.type

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.joshrojas.photosearch.data.remote.response.AuthorResponse

class AuthorTypeAdapter : TypeAdapter<AuthorResponse>() {
    override fun write(out: JsonWriter?, value: AuthorResponse?) {
        // empty
    }

    override fun read(`in`: JsonReader?): AuthorResponse {
        return `in`?.let {
            val value = it.nextString()
            val items = value.split(" ")
            AuthorResponse(items.first(), items[1].removePrefix("(\"").removeSuffix("\")"))
        } ?: AuthorResponse()
    }
}