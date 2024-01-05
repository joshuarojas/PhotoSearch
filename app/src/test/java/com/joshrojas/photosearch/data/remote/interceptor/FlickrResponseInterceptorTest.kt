package com.joshrojas.photosearch.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FlickrResponseInterceptorTest {

    @Mock
    private lateinit var chain: Interceptor.Chain

    private val interceptor = FlickrResponseInterceptor()
    private val request = Request.Builder()
        .url("https://www.flickr.com/services/feeds/photos_public.gne?format=json").build()
    private val responseBuilder = Response.Builder()
        .code(200)
        .request(request)
        .protocol(Protocol.HTTP_2)
        .message("")

    @Before
    fun setUp() {
        Mockito.`when`(chain.request()).thenReturn(request)
    }

    @Test
    fun intercept_RemoveJSONPadding_ExpectedResponse_Successfully() {
        // given
        Mockito.`when`(chain.proceed(request)).thenReturn(
            responseBuilder
                .body(EXPECTED_JSON_RESPONSE.toResponseBody("text/plain".toMediaType()))
                .build()
        )

        // when
        val response = interceptor.intercept(chain)

        // then
        with(response.body?.string().orEmpty()) {
            Assert.assertFalse(startsWith("jsonFlickrFeed("))
            Assert.assertFalse(endsWith(")"))
            Assert.assertTrue(startsWith("{"))
            Assert.assertTrue(endsWith("}"))
        }
    }

    @Test
    fun intercept_RemoveJSONPadding_CleanResponse_Successfully() {
        // given
        Mockito.`when`(chain.proceed(request)).thenReturn(
            responseBuilder
                .body(CLEAN_JSON_RESPONSE.toResponseBody("text/plain".toMediaType()))
                .build()
        )

        // when
        val response = interceptor.intercept(chain)

        // then
        with(response.body?.string().orEmpty()) {
            Assert.assertFalse(startsWith("jsonFlickrFeed("))
            Assert.assertFalse(endsWith(")"))
            Assert.assertTrue(startsWith("{"))
            Assert.assertTrue(endsWith("}"))
        }
    }

    @Test
    fun intercept_RemoveJSONPadding_PlainText_Successfully() {
        // given
        Mockito.`when`(chain.proceed(request)).thenReturn(
            responseBuilder
                .body(WRONG_JSON_RESPONSE.toResponseBody("text/plain".toMediaType()))
                .build()
        )

        // when
        val response = interceptor.intercept(chain)

        // then
        with(response.body?.string().orEmpty()) {
            Assert.assertFalse(startsWith("jsonFlickrFeed("))
            Assert.assertFalse(endsWith(")"))
        }
    }

    companion object {
        private const val EXPECTED_JSON_RESPONSE = "jsonFlickrFeed({\n" +
                "  \"title\": \"Uploads from everyone\",\n" +
                "  \"link\": \"https://www.flickr.com/photos/\",\n" +
                "  \"description\": \"\",\n" +
                "  \"modified\": \"2024-01-04T18:58:55Z\",\n" +
                "  \"generator\": \"https://www.flickr.com\",\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"title\": \" \",\n" +
                "      \"link\": \"https://www.flickr.com/photos/freiyaaa/53441540017/\",\n" +
                "      \"media\": {\n" +
                "        \"m\": \"https://live.staticflickr.com/65535/53441540017_3b7d0dcb9b_m.jpg\"\n" +
                "      },\n" +
                "      \"date_taken\": \"2023-10-02T06:11:13-08:00\",\n" +
                "      \"description\": \" <p><a href=\\\"https://www.flickr.com/people/freiyaaa/\\\">Freiyaaa</a> posted a photo:</p> <p><a href=\\\"https://www.flickr.com/photos/freiyaaa/53441540017/\\\" title=\\\" \\\"><img src=\\\"https://live.staticflickr.com/65535/53441540017_3b7d0dcb9b_m.jpg\\\" width=\\\"159\\\" height=\\\"240\\\" alt=\\\" \\\" /></a></p> <p>Processed with VSCO with v3 preset</p> \",\n" +
                "      \"published\": \"2024-01-04T18:58:55Z\",\n" +
                "      \"author\": \"nobody@flickr.com (\\\"Freiyaaa\\\")\",\n" +
                "      \"author_id\": \"97270601@N08\",\n" +
                "      \"tags\": \"\"\n" +
                "    }\n" +
                "  ]\n" +
                "})"

        private const val CLEAN_JSON_RESPONSE = "{\n" +
                "  \"title\": \"Uploads from everyone\",\n" +
                "  \"link\": \"https://www.flickr.com/photos/\",\n" +
                "  \"description\": \"\",\n" +
                "  \"modified\": \"2024-01-04T18:58:55Z\",\n" +
                "  \"generator\": \"https://www.flickr.com\",\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"title\": \" \",\n" +
                "      \"link\": \"https://www.flickr.com/photos/freiyaaa/53441540017/\",\n" +
                "      \"media\": {\n" +
                "        \"m\": \"https://live.staticflickr.com/65535/53441540017_3b7d0dcb9b_m.jpg\"\n" +
                "      },\n" +
                "      \"date_taken\": \"2023-10-02T06:11:13-08:00\",\n" +
                "      \"description\": \" <p><a href=\\\"https://www.flickr.com/people/freiyaaa/\\\">Freiyaaa</a> posted a photo:</p> <p><a href=\\\"https://www.flickr.com/photos/freiyaaa/53441540017/\\\" title=\\\" \\\"><img src=\\\"https://live.staticflickr.com/65535/53441540017_3b7d0dcb9b_m.jpg\\\" width=\\\"159\\\" height=\\\"240\\\" alt=\\\" \\\" /></a></p> <p>Processed with VSCO with v3 preset</p> \",\n" +
                "      \"published\": \"2024-01-04T18:58:55Z\",\n" +
                "      \"author\": \"nobody@flickr.com (\\\"Freiyaaa\\\")\",\n" +
                "      \"author_id\": \"97270601@N08\",\n" +
                "      \"tags\": \"\"\n" +
                "    }\n" +
                "  ]\n" +
                "}"

        private const val WRONG_JSON_RESPONSE = "..."
    }
}