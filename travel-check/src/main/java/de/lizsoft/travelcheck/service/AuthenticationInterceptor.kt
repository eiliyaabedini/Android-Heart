package de.lizsoft.travelcheck.service

import okhttp3.Interceptor
import okhttp3.Response

class AuthenticationInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        builder.addHeader("authentication", "Bearer fffffffffff")

        return chain.proceed(builder.build())
    }
}
