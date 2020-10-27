package com.yan.ahtpodcast002.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.yan.ahtpodcast002.entities.models.Episode
import com.yan.ahtpodcast002.entities.models.Podcast
import com.yan.ahtpodcast002.repository.PodcastRepository
import com.yan.ahtpodcast002.utils.DateUtils
import java.util.*

class PodcastViewModel(application: Application) : AndroidViewModel(application) {

    var podcastRepository: PodcastRepository? = null
    var activePodcastViewData: PodcastViewData? = null
    private var activePodcast: Podcast? = null
    var livePodcastData: LiveData<List<SearchViewModel.PodcastSummaryViewData>>? = null


    data class PodcastViewData(
        var subscribed: Boolean = false,
        var feedTitle: String? = "",
        var feedUrl: String? = "",
        var feedDesc: String? = "",
        var imageUrl: String? = "",
        var episodes: List<EpisodeViewData>?
    )

    data class EpisodeViewData(
        var guid: String? = "",
        var title: String? = "",
        var description: String? = "",
        var mediaUrl: String? = "",
        var releaseDate: Date? = null,
        var duration: String? = ""
    )

    private fun episodesToEpisodesView(episodes: List<Episode>): List<EpisodeViewData> {
        Log.d("PodcastViewModel", "${episodes}")
        return episodes.map {
            EpisodeViewData(
                it.guid, it.title, it.description, it.mediaUrl,
                it.releaseDate, it.duration
            )
        }
    }

    private fun podcastToPodcastView(podcast: Podcast): PodcastViewData {
        return PodcastViewData(
            podcast.id != null,
            podcast.feedTitle,
            podcast.feedUrl,
            podcast.feedDesc,
            podcast.imageUrl,
            episodesToEpisodesView(podcast.episodes)
        )
    }

    private fun podcastToSummaryView(podcast: Podcast):
            SearchViewModel.PodcastSummaryViewData {
        return SearchViewModel.PodcastSummaryViewData(
            podcast.feedTitle,
            DateUtils.dateToShortDate(podcast.lastUpdated),
            podcast.imageUrl,
            podcast.feedUrl
        )
    }

    fun getPodcast(
        podcastSummaryViewData: SearchViewModel.PodcastSummaryViewData,
        callback: (PodcastViewData?) -> Unit
    ) {
        val repo = podcastRepository ?: return
        val feedUrl = podcastSummaryViewData.feedUrl ?: return
        repo.getPodcast(feedUrl) {
            it?.let {
                it.feedTitle = podcastSummaryViewData.name ?: ""
                it.imageUrl = podcastSummaryViewData.imageUrl ?: ""
                activePodcastViewData = podcastToPodcastView(it)
                activePodcast = it
                callback(activePodcastViewData)
            }
        }
    }

    fun saveActivePodcast() {
        val repo = podcastRepository ?: return
        activePodcast?.let {

            repo.save(it)
        }
    }

    fun getPodcasts(): LiveData<List<SearchViewModel.PodcastSummaryViewData>>? {
        val repo = podcastRepository ?: return null
// 1
        if (livePodcastData == null) {
// 2
            val liveData = repo.getAll()
// 3
            livePodcastData = Transformations.map(liveData) { podcastList ->
                podcastList.map { podcast ->
                    podcastToSummaryView(podcast)
                }
            }
        }
// 4
        return livePodcastData
    }

    fun deleteActivePodcast() {
        val repo = podcastRepository ?: return
        activePodcast?.let {
            repo.delete(it)
        }
    }

    fun setActivePodcast(
        feedUrl: String, callback:
            (SearchViewModel.PodcastSummaryViewData?) -> Unit
    ) {
        val repo = podcastRepository ?: return
        repo.getPodcast(feedUrl) {
            if (it == null) {
                callback(null)
            } else {
                activePodcastViewData = podcastToPodcastView(it)
                activePodcast = it
                callback(podcastToSummaryView(it))
            }
        }
    }
}

