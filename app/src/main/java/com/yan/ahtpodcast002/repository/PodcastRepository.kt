package com.yan.ahtpodcast002.repository

import com.yan.ahtpodcast002.entities.database.Podcast

class PodcastRepository {
    fun getPodcast(feedUrl: String, callback: ((Podcast?) -> Unit)) {
        callback(Podcast(feedUrl, "No Name", "No description", "No image"))
    }
}
