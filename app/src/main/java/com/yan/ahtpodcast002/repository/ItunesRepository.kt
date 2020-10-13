package com.yan.ahtpodcast002.repository

import com.yan.ahtpodcast002.entities.remote.ItunesPodcast
import com.yan.ahtpodcast002.entities.remote.PodcastResponse
import com.yan.ahtpodcast002.service.ItunesService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 1
class ItunesRepository(private val itunesService: ItunesService) {
    // 2
    fun searchByTerm(
        term: String,
        callBack: (List<ItunesPodcast>?) -> Unit
    ) {
        // 3
        val podcastCall = itunesService.searchPodcastByTerm(term)
        // 4
        podcastCall.enqueue(object : Callback<PodcastResponse> {
            // 5
            override fun onFailure(
                call: Call<PodcastResponse>?,
                t: Throwable?
            ) {
        // 6
                callBack(null)
            }

            // 7
            override fun onResponse(
                call: Call<PodcastResponse>?,
                response: Response<PodcastResponse>?
            ) {
            // 8
                val body = response?.body()
            // 9
                callBack(body?.results)
            }
        })
    }
}