package com.alfaazplus.sunnah.api

import com.alfaazplus.sunnah.api.models.AppUpdate
import com.alfaazplus.sunnah.api.models.AppUrls
import com.alfaazplus.sunnah.api.models.ResourcesVersions
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface GithubApi {
    @GET("inventory/versions/app_updates.json")
    suspend fun getAppUpdates(): List<AppUpdate>

    @GET("inventory/versions/resources_versions.json")
    suspend fun getResourcesVersions(): ResourcesVersions

    @GET("inventory/other/urls.json")
    suspend fun getAppUrls(): AppUrls

    @GET("inventory/collections/{collectionId}/base.tar.bz2")
    @Streaming
    suspend fun getCollection(@Path("collectionId") collectionId: Int): Response<ResponseBody>

    @GET("inventory/collections/{collectionId}/{locale}.tar.bz2")
    @Streaming
    suspend fun getCollectionTranslation(
        @Path("collectionId") collectionId: Int,
        @Path("locale") locale: String
    ): Response<ResponseBody>
}
