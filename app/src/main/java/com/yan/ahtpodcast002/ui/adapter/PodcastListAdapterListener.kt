package com.yan.ahtpodcast002.ui.adapter

import com.yan.ahtpodcast002.viewmodels.SearchViewModel

interface PodcastListAdapterListener {
    fun onShowDetails(podcastSummaryViewData: SearchViewModel.PodcastSummaryViewData)
}
