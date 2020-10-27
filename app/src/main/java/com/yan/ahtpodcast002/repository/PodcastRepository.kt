package com.yan.ahtpodcast002.repository

import androidx.lifecycle.LiveData
import com.yan.ahtpodcast002.database.PodcastDao
import com.yan.ahtpodcast002.entities.models.Episode
import com.yan.ahtpodcast002.entities.models.Podcast
import com.yan.ahtpodcast002.entities.rss.RssFeedResponse
import com.yan.ahtpodcast002.service.FeedService
import com.yan.ahtpodcast002.service.RssFeedService
import com.yan.ahtpodcast002.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PodcastRepository(
    private var feedService: FeedService,
    private var podcastDao: PodcastDao
) {
    //    fun getPodcast(feedUrl: String, callback: ((Podcast?) -> Unit)) {
////        /***/
////        GlobalScope.launch {
////            val podcast = podcastDao.loadPodcast(feedUrl)
////            if (podcast != null) {
////                podcast.id?.let {
////                    podcast.episodes = podcastDao.loadEpisodes(it)
////                    GlobalScope.launch(Dispatchers.Main) {
////                        callback(podcast)
////                    }
////                }
////            } else {
////                /***/
////                val rssFeedService = RssFeedService()
////                rssFeedService.getFeed(feedUrl) { rssFeedResponse ->
////                    var podcast: Podcast? = null
////                    // if the feedResponse is null, pass null to the callback method, if the  response is valid, then you convert it to a podcast object and pass it to the callback method
////                    if (rssFeedResponse != null) {
////                        podcast = rssResponseToPodcast(feedUrl, "", rssFeedResponse)
////                    }
////
////                    GlobalScope.launch(Dispatchers.Main) {
////                        callback(podcast)
////                    }
////
////                }
////                callback(Podcast(null, feedUrl, "No Name", "No description", "No image"))
////            }
////        }
////    }
    fun getPodcast(feedUrl: String, callback: (Podcast?) -> Unit) {

        GlobalScope.launch {

            val podcast = podcastDao.loadPodcast(feedUrl)

            if (podcast != null) {
                podcast.id?.let {
                    podcast.episodes = podcastDao.loadEpisodes(it)
                    GlobalScope.launch(Dispatchers.Main) {
                        callback(podcast)
                    }
                }
            } else {

                feedService.getFeed(feedUrl) { feedResponse ->

                    if (feedResponse == null) {
                        GlobalScope.launch(Dispatchers.Main) {
                            callback(null)
                        }
                    } else {
                        val podcast = rssResponseToPodcast(feedUrl, "", feedResponse)
                        GlobalScope.launch(Dispatchers.Main) {
                            callback(podcast)
                        }
                    }
                }
            }
        }

    }

    /***/

    // Helper method to convert the RSS response data into Episode and Podcast Object
    private fun rssItemToEpisodes(episodeResponses: List<RssFeedResponse.EpisodeResponse>): List<Episode> {
        return episodeResponses.map {

            Episode(
                it.guid ?: "",
                null,
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
        val description =
            if (rssResponse.description == "") rssResponse.summary else rssResponse.description
        //3 create a new Podcast object and return it to the caller
        return Podcast(
            null,
            feedUrl,
            rssResponse.title,
            description,
            imageUrl,
            rssResponse.lastUpdated,
            episodes = rssItemToEpisodes(items)
        )
    }

    fun save(podcast: Podcast) {
        GlobalScope.launch {
            val podcastId = podcastDao.insertPodcast(podcast)
            for (episode in podcast.episodes) {
                episode.podcastId = podcastId
                podcastDao.insertEpisode(episode)
            }
        }
    }

    fun getAll(): LiveData<List<Podcast>> {
        return podcastDao.loadPodcasts()
    }

    fun delete(podcast: Podcast) {
        GlobalScope.launch {
            podcastDao.deletePodcast(podcast)
        }
    }

    /**Chap 25*/

    // Method that takes a single podcast and returns a list of new episodes
    private fun getNewEpisodes(
        localPodcast: Podcast, callBack:
            (List<Episode>) -> Unit
    ) {
// 1    User feedService to download the latest episode
        feedService.getFeed(localPodcast.feedUrl) { response ->
            if (response != null) {
// 2    Convert the feedService response to the remote podcast obj
                val remotePodcast =
                    rssResponseToPodcast(
                        localPodcast.feedUrl,
                        localPodcast.imageUrl, response
                    )
                remotePodcast?.let {
// 3    Load the list of local episodes from the database
                    val localEpisodes =
                        podcastDao.loadEpisodes(localPodcast.id!!)
// 4    Filter the remotePodcast episodes to contain only the ones that are not found in the local Episodes list and assign to the newEpisode
                    val newEpisodes = remotePodcast.episodes.filter { episode ->
                        localEpisodes.find {
                            episode.guid == it.guid
                        } == null
                    }
// 5    Pass the newEpisodes list to the callback method
                    callBack(newEpisodes)
                }
            } else {
// 6    Return an empty list if the feedService does not return a response
                callBack(listOf())
            }
        }
    }

    //      Method that updates an existing podcast with a new episode
//    This method inserts the list of episodes into the database for the given podcastId
    private fun saveNewEpisodes(
        podcastId: Long, episodes:
        List<Episode>
    ) {
        GlobalScope.launch {
            for (episode in episodes) {
                episode.podcastId = podcastId
                podcastDao.insertEpisode(episode)
            }
        }
    }

//    updatePodcastEpisodes

    fun updatePodcastEpisodes(
        callback: (List<PodcastUpdateInfo>) -> Unit
    ) {
// 1    Initilize an empty list of PodcastUpdateInfo objects
        val updatedPodcasts: MutableList<PodcastUpdateInfo> = mutableListOf()
// 2    Load the subscribed podcasts from the database w/o the LiveData wrapping
        val podcasts = podcastDao.loadPodcastsStatic()
// 3    Keep track of the background processing
        var processCount = podcasts.count()
// 4    The podcast are accessed one at a time
        for (podcast in podcasts) {
// 5    When processCount reaches 0, => Time to pass the updatedPodcast list to the callback method
            getNewEpisodes(podcast) { newEpisodes ->
// 6    If there were new episodes, they are inserted to the database, the updatedPodcasts List will be appended
                if (newEpisodes.count() > 0) {
                    saveNewEpisodes(podcast.id!!, newEpisodes)
                    updatedPodcasts.add(
                        PodcastUpdateInfo(
                            podcast.feedUrl,
                            podcast.feedTitle, newEpisodes.count()
                        )
                    )
                }
// 7    ProcessCount = 0 => All podcasts were processed
                processCount--
                if (processCount == 0) {
// 8    pass the list to the updated podcasts
                    callback(updatedPodcasts)
                }
            }
        }
    }

    class PodcastUpdateInfo(val feedUrl: String, val name: String, val newCount: Int)

    /**End chap 25*/
}


