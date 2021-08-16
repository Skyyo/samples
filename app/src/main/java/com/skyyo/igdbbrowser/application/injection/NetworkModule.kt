package com.skyyo.igdbbrowser.application.injection

import com.skyyo.igdbbrowser.BuildConfig
import com.skyyo.igdbbrowser.application.network.calls.AuthCalls
import com.skyyo.igdbbrowser.application.network.calls.GamesCalls
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
                addInterceptor(loggingInterceptor)
            }
            connectTimeout(timeout = 10, TimeUnit.SECONDS)
            writeTimeout(timeout = 10, TimeUnit.SECONDS)
            readTimeout(timeout = 10, TimeUnit.SECONDS)
        }.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: Lazy<OkHttpClient>): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .callFactory { request -> okHttpClient.get().newCall(request) }
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideAuthCalls(retrofit: Retrofit): AuthCalls = retrofit.create(AuthCalls::class.java)

    @Singleton
    @Provides
    fun provideGamesCalls(retrofit: Retrofit): GamesCalls = retrofit.create(GamesCalls::class.java)

}
