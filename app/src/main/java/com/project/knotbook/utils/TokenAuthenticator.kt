package com.project.knotbook.utils

import com.project.knotbook.api.RefreshAuthApi
import com.project.knotbook.model.RefreshTokenRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val refreshAuthApi: RefreshAuthApi
) : Authenticator {

    private val lock = Any()

    override fun authenticate(
        route: Route?,
        response: Response
    ): Request? {

        // Avoid infinite retry loops
        //checks how many times OkHttp has attempted this request.
        if (responseCount(response) >= 2) {
            return null
        }

        //ensures only one thread makes the network call to refresh the token.
        synchronized(lock) {

            return runBlocking {

                val currentAccessToken =
                    tokenDataStore.accessToken.first()

                val failedAccessToken =
                    response.request.header("Authorization")
                        ?.removePrefix("Bearer ")

                // Another request may already have refreshed the token.
                // Reuse that token instead of refreshing again.
                if (
                    !currentAccessToken.isNullOrBlank() &&
                    currentAccessToken != failedAccessToken
                ) {
                    return@runBlocking response.request
                        .newBuilder()
                        .header(
                            "Authorization",
                            "Bearer $currentAccessToken"
                        )
                        .build()
                }

                val refreshToken =
                    tokenDataStore.refreshToken.first()

                if (refreshToken.isNullOrBlank()) {
                    tokenDataStore.clearTokens()
                    return@runBlocking null
                }

                try {
                    val newTokens = refreshAuthApi.refreshToken(
                        RefreshTokenRequest(refreshToken)
                    )

                    // Save both rotated tokens
                    tokenDataStore.saveTokens(
                        accessToken = newTokens.accessToken,
                        refreshToken = newTokens.refreshToken
                    )

                    // Retry original request with new access token
                    response.request
                        .newBuilder()
                        .header(
                            "Authorization",
                            "Bearer ${newTokens.accessToken}"
                        )
                        .build()

                } catch (e: HttpException) {

                    // Invalid, expired or revoked refresh token
                    if (e.code() == 401 || e.code() == 403) {
                        tokenDataStore.clearTokens()
                    }

                    null

                } catch (e: IOException) {
                    // Network unavailable: preserve existing tokens
                    null

                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse

        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }

        return count
    }
}