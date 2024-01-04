package com.joshrojas.photosearch.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody

class FlickrResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val path = request.url.toUri().path
        val response: Response = chain.proceed(request)
        return if (response.isSuccessful) {
            var body: ResponseBody? = null
            if (path.contains("/feeds/")) {
                body = response.body?.let {
                    val bodyString = it.string()
                        .removePrefix("jsonFlickrFeed(")
                        .removeSuffix(")")
                    val contentType = it.contentType()
                    bodyString.toResponseBody(contentType)
                }
            }
            response.newBuilder().body(body).build()
        } else {
            response
        }
    }

}