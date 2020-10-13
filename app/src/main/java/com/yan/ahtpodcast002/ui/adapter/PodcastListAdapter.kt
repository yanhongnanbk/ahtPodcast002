package com.yan.ahtpodcast002.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yan.ahtpodcast002.R
import com.yan.ahtpodcast002.viewmodels.SearchViewModel
import kotlinx.android.synthetic.main.item_search.view.*

class PodcastListAdapter(
    private var podcastSummaryViewList: List<SearchViewModel.PodcastSummaryViewData>?,
    private var podcastListAdapterListener: PodcastListAdapterListener,
    private var parentActivity: Activity

) : RecyclerView.Adapter<PodcastListAdapter.ViewHolder>() {


    inner class ViewHolder(
        v: View,
        private val podcastListAdapterListener: PodcastListAdapterListener
    ) : RecyclerView.ViewHolder(v) {
        var podcastSummaryViewData: SearchViewModel.PodcastSummaryViewData? = null
        val nameTextView: TextView = v.podcastNameTextView
        val lastUpdatedTextView: TextView = v.podcastLastUpdatedTextView
        val podcastImageView: ImageView = v.podcastImage

        init {
            v.setOnClickListener {

                podcastSummaryViewData?.let {
                    podcastListAdapterListener.onShowDetails(it)
                }
            }
        }
    }

    fun setSearchData(podcastSummaryViewData: List<SearchViewModel.PodcastSummaryViewData>) {
        podcastSummaryViewList = podcastSummaryViewData
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        TODO("Not yet implemented")
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search, parent, false), podcastListAdapterListener
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        TODO("Not yet implemented")

        val searchViewList = podcastSummaryViewList ?: return
        val searchView = searchViewList[position]
        holder.podcastSummaryViewData = searchView
        holder.nameTextView.text = searchView.name
        holder.lastUpdatedTextView.text = searchView.lastUpdated

        Glide.with(parentActivity)
            .load(searchView.imageUrl)
            .into(holder.podcastImageView)


    }

    override fun getItemCount(): Int {
//        TODO("Not yet implemented")
        return podcastSummaryViewList?.size ?: 0
    }
}