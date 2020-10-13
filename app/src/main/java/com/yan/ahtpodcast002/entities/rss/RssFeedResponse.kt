package com.yan.ahtpodcast002.entities.rss

import java.time.LocalDateTime
import java.util.*

data class RssFeedResponse(

    var title: String = "",
    var description: String = "",
    var summary: String = "",
    var lastUpdated: Date = Date(),
//    var lastUpdated1: String = "",
    var episodes: MutableList<EpisodeResponse>? = null

) {
    data class EpisodeResponse(
        var title: String? = null,
        var link: String? = null,
        var description: String? = null,
        var guid: String? = null, // unique id for the episode
        var pubDate: String? = null,
        var duration: String? = null,
        var url: String? = null,
        var type: String? = null,
    )
}