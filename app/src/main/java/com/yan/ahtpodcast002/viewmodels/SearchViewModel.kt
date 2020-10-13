package com.yan.ahtpodcast002.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.yan.ahtpodcast002.entities.remote.ItunesPodcast
import com.yan.ahtpodcast002.repository.ItunesRepository
import com.yan.ahtpodcast002.utils.DateUtils

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    var itunesRepository: ItunesRepository? = null

    data class PodcastSummaryViewData(

        var name: String? = "",
        var lastUpdated: String? = "",
        var imageUrl: String? = "",
        var feedUrl: String? = ""
    )

    private fun itunesPodcastToPodcastSummaryView(itunesPodcast: ItunesPodcast): PodcastSummaryViewData {

        return PodcastSummaryViewData(
            itunesPodcast.collectionCensoredName,
            DateUtils.jsonDateToShortDate(itunesPodcast.releaseDate),
            itunesPodcast.artworkUrl30,
            itunesPodcast.feedUrl
        )

    }

    //1 The first parameter is the search term. The callback parameter is a method that's called with the results, Since the iTunes repo's search method runs async, this method needs a way to let its caller know when the work is done.

    // Unit similar to void in java
    fun searchPodcasts(term: String, callback: (List<PodcastSummaryViewData>) -> Unit) {
        //2 iTunes'repo is used to perform the search async
        itunesRepository?.searchByTerm(term) { results ->
            if (results == null) {
                //3 If the result is null, pass an empty list to the callback method
                callback(emptyList())
            } else {
                //4 if the results are not null, then you map them to the PodcastSummaryViewData obj. => providing View with enough data for the presentation
                val searchViews = results.map { itunesPodcast ->
                    itunesPodcastToPodcastSummaryView(itunesPodcast)
                }
                //5 pass the mapped results to the callback method so you can display them
                Log.d("SEARCHVIEWMODEL", "${callback(searchViews)}")
                callback(searchViews)

            }
        }
    }

}