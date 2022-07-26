package com.ebpearls.api

import com.google.gson.Gson
import com.introdating.app.data.api.TokenRefreshManager
import com.nando.codegen.data.ApiContract.NetworkCodes.UNAUTHENTICATED
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import okio.GzipSource
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import javax.inject.Inject


class AuthorizationInterceptor @Inject constructor(
    private val tokenRefreshManager: TokenRefreshManager,
    private val prefsDataStoreManager: PrefsDataStoreManager,
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking {
            return@runBlocking prefsDataStoreManager.getAccessToken().first()
        }
        var request: Request
        var response: Response
        try {
            request = chain.request().newBuilder()
                .addHeader(name = ApiContract.Keys.AUTHORIZATION, value = accessToken)
                .build()
            response = chain.proceed(request)
        } catch (e: Exception) {
            throw e
        }

        //region parse response to handle accessToken with refreshToken
        val responseBody = response.body!!
        val contentLength = responseBody.contentLength()
        val headers = response.headers
        val source = responseBody.source()
        source.request(Long.MAX_VALUE) // Buffer the entire body.
        var buffer = source.buffer
        if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
            GzipSource(buffer.clone()).use { gzippedResponseBody ->
                buffer = Buffer()
                buffer.writeAll(gzippedResponseBody)
            }
        }
        val contentType = responseBody.contentType()
        val charset: Charset =
            contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8

        if (contentLength != 0L) {
            var newAccessToken = ""
            val responseString = buffer.clone().readString(charset)
            val apiError = getApiError(responseString)
            if (apiError?.data == null
                && !apiError?.errors.isNullOrEmpty()
                && apiError?.errors?.get(0)?.extensions?.code == UNAUTHENTICATED
            ) {
                newAccessToken = runBlocking {
                    return@runBlocking tokenRefreshManager.doRefreshAccessToken()
                }
            }
            if (newAccessToken.isNotEmpty()) {
                try {
                    request = chain.request().newBuilder()
                        .addHeader(name = ApiContract.Keys.AUTHORIZATION, value = newAccessToken)
                        .build()
                    response = chain.proceed(request)
                } catch (e: Exception) {
                    throw e
                }
            }
        }
        //endregion

        return response
    }

    /**
     * @param errorResponse to parse error of [ApiError]
     */
    private fun getApiError(errorResponse: String): ApiError? {
        if (errorResponse.isEmpty())
            return null
        return try {
            Gson().fromJson(
                errorResponse,
                ApiError::class.java
            )
        } catch (e: Throwable) {
            null
        }
    }
}