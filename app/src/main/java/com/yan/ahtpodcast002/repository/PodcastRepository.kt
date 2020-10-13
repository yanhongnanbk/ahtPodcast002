package com.yan.ahtpodcast002.repository

import com.yan.ahtpodcast002.entities.database.Episode
import com.yan.ahtpodcast002.entities.database.Podcast
import com.yan.ahtpodcast002.entities.rss.RssFeedResponse
import com.yan.ahtpodcast002.service.FeedService
import com.yan.ahtpodcast002.service.RssFeedService
import com.yan.ahtpodcast002.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PodcastRepository(private var feedService: FeedService) {
    fun getPodcast(feedUrl: String, callback: ((Podcast?) -> Unit)) {
        val rssFeedService = RssFeedService()
        rssFeedService.getFeed(feedUrl) {

            rssFeedResponse ->
            var podcast:Podcast?=null
            // if the feedResponse is null, pass null to the callback method, if the  response is valid, then you convert it to a podcast object and pass it to the callback method
            if (rssFeedResponse!=null){
                podcast = rssResponseToPodcast(feedUrl, "", rssFeedResponse)
            }

            GlobalScope.launch(Dispatchers.Main){
                callback(podcast)
            }

        }
        callback(Podcast(feedUrl, "No Name", "No description", "No image"))
    }

    // Helper method to convert the RSS response data into Episode and Podcast Object
    private fun rssItemToEpisodes(episodeResponses: List<RssFeedResponse.EpisodeResponse>): List<Episode> {
        return episodeResponses.map {

            Episode(
                it.guid ?: "",
                it.title ?: "",
                it.description ?: "",
                it.url ?: "",
                it.type ?: "",
                DateUtils.xmlToDate(it.pubDate),
                it.duration ?: ""
            )
        }
    }

    //Convert Rss Response to a Podcast object
    private fun rssResponseToPodcast(
        feedUrl: String,
        imageUrl: String,
        rssResponse: RssFeedResponse
    ): Podcast? {
        //1 assign the list of episodes to items provided it's not null; otherwise, the method returns null
        val items = rssResponse.episodes ?: return null
        //2 if the description is empty, it is set to the rssResponse.summary, else rssResponse.desc
        val description = if (rssResponse.description == "")
            rssResponse.summary else rssResponse.description
        //3 create a new Podcast object and return it to the caller
        return Podcast(
            feedUrl,
            rssResponse.title,
            description,
            imageUrl,
            rssResponse.lastUpdated,
            episodes = rssItemToEpisodes(items)
        )
    }
}
