package com.introdating.app.data.api

import RefreshTokenMutation
import android.content.Context
import android.content.Intent
import com.apollographql.apollo.coroutines.await
import com.introdating.app.RefreshTokenMutation
import com.introdating.app.data.DataStringsModel
import com.introdating.app.data.prefs.PrefsDataStoreManager
import com.introdating.app.type.RefreshTokenBody
import com.introdating.app.util.contracts.AppContracts
import com.nando.codegen.data.AuthTokenApiServices
import kotlinx.coroutines.flow.first
import type.RefreshTokenBody
import javax.inject.Inject

class TokenRefreshManager @Inject constructor(
    private val context: Context,
    private val apiServices: AuthTokenApiServices,
    private val prefsDataStoreManager: PrefsDataStoreManager,
) {
    /**
     * OkHttp will automatically ask the Authenticator for credentials
     * when a response is 401 Not Authorised retrying last failed request with them.
     *
     * @see [https://stackoverflow.com/questions/32354098/okhttp-authenticator-multithreading]
     */
    suspend fun doRefreshAccessToken(): String {
        // The request will contain "refreshToken" as header if the last failed request was to refresh access token.
        // This means that while attempting to refresh access token, 401- authentication error was encountered.
        // So, the token is considered invalid and user's session is ended.
        try {
            val refreshTokenOld = prefsDataStoreManager.getRefreshToken().first()
            if (refreshTokenOld.isEmpty()) {
                handleSessionExpiry(null)
                return ""
            } else {
                // Refresh your access_token using a synchronous api request
                val refreshTokenResponse =
                    apiServices.client.mutate(
                        RefreshTokenMutation(
                            RefreshTokenBody(refreshTokenOld)
                        )
                    ).await()
                if (!refreshTokenResponse.hasErrors()) {
                    val refreshToken = refreshTokenResponse.data?.refreshToken
                    val newAccessToken: String =
                        refreshToken?.fragments?.tokenFragment?.accessToken ?: ""
                    val newAccessTokenExpireIn: String =
                        refreshToken?.fragments?.tokenFragment?.accessTokenExpiresIn ?: ""
                    val newRefreshToken: String =
                        refreshToken?.fragments?.tokenFragment?.refreshToken ?: ""
                    val newRefreshTokenExpireIn: String =
                        refreshToken?.fragments?.tokenFragment?.refreshTokenExpiresIn ?: ""
                    return if (newAccessToken.isNotEmpty() && newRefreshToken.isNotEmpty()) {
                        prefsDataStoreManager.apply {
                            setAccessToken(newAccessToken)
                            setAccessTokenExpiresIn(newAccessTokenExpireIn)
                            setRefreshToken(newRefreshToken)
                            setRefreshTokenExpiresIn(newRefreshTokenExpireIn)
                        }
                        // Add new header to rejected request and retry it
                        newAccessToken
                    } else {
                        handleSessionExpiry(refreshTokenResponse.errors?.get(0)?.message)
                        ""
                    }
                } else {
                    handleSessionExpiry(refreshTokenResponse.errors?.get(0)?.message)
                    return ""
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            handleSessionExpiry(e.message)
            return ""
        }
    }

    private fun handleSessionExpiry(
        errMsg: String?
    ) {
        val message = errMsg ?: DataStringsModel.authenticationError.get()!!
        sendSessionExpiredBroadCast(message)
    }

    private fun sendSessionExpiredBroadCast(errMsg: String) {
        val intent = Intent().also {
            it.putExtra(AppContracts.Extra.MESSAGE, errMsg)
            it.action = AppContracts.Action.SESSION_EXPIRED
        }
        context.sendBroadcast(intent)
    }
}