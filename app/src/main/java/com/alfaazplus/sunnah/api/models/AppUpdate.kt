/*
 * Copyright (c) Faisal Khan (https://github.com/faisalcodes)
 * Created on 3/2/2023.
 * All rights reserved.
 */

package com.alfaazplus.sunnah.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppUpdate(
    val version: Long,
    @SerialName("updatePriority") val priority: Int
)
