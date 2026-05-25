package com.alfaazplus.sunnah.api

import com.alfaazplus.sunnah.api.models.AppUpdate
import com.alfaazplus.sunnah.api.models.ResourcesVersions
import retrofit2.http.GET

interface GithubApi {
    @GET("inventory/versions/app_updates.json")
    suspend fun getAppUpdates(): List<AppUpdate>

    @GET("inventory/versions/resources_versions.json")
    suspend fun getResourcesVersions(): ResourcesVersions
}
