package com.example.passwordapp.network

import com.squareup.moshi.Json


data class IconResponseData(
    val icons: List<Icon>
)

data class Icon(
    @Json(name = "raster_sizes") val rasterSizes: List<RasterSize>
)

data class RasterSize(
    val formats: List<IconFormat>,
    val size: Int
)

data class IconFormat(
    @Json(name = "preview_url") val previewUrl: String
)