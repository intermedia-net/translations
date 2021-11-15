package com.intermedia.translations.lokalise

import okhttp3.Interceptor
import okhttp3.Response

class LokaliseInterceptor(
    private val apiToken: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("x-api-token", apiToken)
            .addHeader("content-type", "application/json")
            .build()
        return chain.proceed(request)
    }
}
