package com.yan.ahtpodcast002.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yan.ahtpodcast002.R
import com.yan.ahtpodcast002.viewmodels.PodcastViewModel
import kotlinx.android.synthetic.main.item_episode.view.*

class EpisodeListAdapter(private var episodeViewList: List<PodcastViewModel.EpisodeViewData>?) :
    RecyclerView.Adapter<EpisodeListAdapter.ViewHolder>() {
    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var episodeViewData: PodcastViewModel.EpisodeViewData? = null
        val titleTextView: TextView = v.titleView
        val descTextView: TextView = v.descView
        val durationTextView: TextView = v.durationView
        val releaseDateTextView: TextView = v.releaseDateView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_episode, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episodeViewList = episodeViewList ?: return
        val episodeView = episodeViewList[position]

        holder.episodeViewData = episodeView
        holder.titleTextView.text = episodeView.title
        holder.descTextView.text = episodeView.description
        holder.durationTextView.text = episodeView.duration
        holder.releaseDateTextView.text = episodeView.releaseDate.toString()
    }

    override fun getItemCount(): Int {
        return episodeViewList?.size ?: 0
    }
}