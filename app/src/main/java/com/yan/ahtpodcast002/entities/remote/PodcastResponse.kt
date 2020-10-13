package com.yan.ahtpodcast002.entities.remote


import com.google.gson.annotations.SerializedName

data class PodcastResponse(
    @SerializedName("resultCount")
    val resultCount: Int,
    @SerializedName("results")
    val results: List<ItunesPodcast>
)