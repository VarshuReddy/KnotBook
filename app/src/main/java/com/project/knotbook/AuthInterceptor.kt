package com.project.knotbook

import com.project.knotbook.utils.TokenDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenDataStore: TokenDataStore): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response{

        val originalRequest = chain.request()

        if (originalRequest.url.encodedPath.startsWith("/api/auth/")) {
            return chain.proceed(originalRequest)
        }

        val token = runBlocking {
            tokenDataStore.accessToken.first()
        }

        val request = originalRequest.newBuilder().apply {
            if (!token.isNullOrBlank()) {
                header("Authorization", "Bearer $token")
            }
        }.build()

        return chain.proceed(request)
    }
}