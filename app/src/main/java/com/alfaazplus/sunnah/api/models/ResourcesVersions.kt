package com.alfaazplus.sunnah.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResourcesVersions(
    @SerialName("translations")
    val translations: Map<String, Int>,
)
