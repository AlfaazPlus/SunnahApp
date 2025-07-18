package com.alfaazplus.sunnah.api

import com.alfaazplus.sunnah.Logger
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@OptIn(ExperimentalSerializationApi::class)
object RetrofitInstance {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            Logger.d(chain.request().url())
            return@addInterceptor chain.proceed(chain.request())
        }
        .cache(null)
        .build()

    private var githubApi: GithubApi? = null
    var githubResDownloadUrl: String = ApiConfig.GH_PROXY_ROOT_URL

    val github: GithubApi
        get() {
            Logger.d(githubResDownloadUrl)

            if (githubApi == null) {
                githubApi = Retrofit.Builder()
                    .baseUrl(githubResDownloadUrl)
                    .addConverterFactory(
                        JsonHelper.json.asConverterFactory(MediaType.get("application/json"))
                    )
                    .client(client)
                    .build()
                    .create(GithubApi::class.java)
            }

            return githubApi!!
        }

    fun resetGithubApi() {
        githubApi = null
    }
}
