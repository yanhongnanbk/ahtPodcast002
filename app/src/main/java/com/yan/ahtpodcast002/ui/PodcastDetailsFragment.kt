package com.yan.ahtpodcast002.ui

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.yan.ahtpodcast002.R
import com.yan.ahtpodcast002.ui.adapter.EpisodeListAdapter
import com.yan.ahtpodcast002.viewmodels.PodcastViewModel
import kotlinx.android.synthetic.main.fragment_podcast_details.*

class PodcastDetailsFragment : Fragment() {

    private val podcastViewModel: PodcastViewModel by activityViewModels()
    private lateinit var episodeListAdapter: EpisodeListAdapter
    private var listener: OnPodcastDetailsListener? = null
    private var menuItem: MenuItem? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPodcastDetailsListener) {
            listener = context
        } else {
            throw RuntimeException(
                context.toString() +
                        " must implement OnPodcastDetailsListener"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_podcast_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupControls()
        updateControls()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_details, menu)
        menuItem = menu.findItem(R.id.menu_feed_action)
        updateMenuItem()
    }

    /** Methods*/
    // update Control
    private fun updateControls() {
        val viewData = podcastViewModel.activePodcastViewData ?: return
        feedTitleTextView.text = viewData.feedTitle
        feedDescTextView.text = viewData.feedDesc
        activity?.let { activity ->
            Glide.with(activity).load(viewData.imageUrl)
                .into(feedImageView)
        }
    }

    // Instantiate Fragment in Activity
    companion object {
        fun newInstance(): PodcastDetailsFragment {
            return PodcastDetailsFragment()
        }
    }

    //
    private fun setupControls() {
// 1
        feedDescTextView.movementMethod = ScrollingMovementMethod()
// 2
        episodeRecyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        episodeRecyclerView.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(
            episodeRecyclerView.context, layoutManager.orientation
        )
        episodeRecyclerView.addItemDecoration(dividerItemDecoration)
// 3
        Log.d("PodcastFragment", "${podcastViewModel.activePodcastViewData?.episodes}")
        episodeListAdapter = EpisodeListAdapter(
            podcastViewModel.activePodcastViewData?.episodes
        )
        episodeRecyclerView.adapter = episodeListAdapter
        //3
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_feed_action -> {
                podcastViewModel.activePodcastViewData?.feedUrl?.let {
//                    listener?.onSubscribe()
                    if (podcastViewModel.activePodcastViewData?.subscribed!!) {
                        listener?.onUnsubscribe()
                    } else {
                        listener?.onSubscribe()
                    }
                }

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    private fun updateMenuItem() {

// 1
        val viewData = podcastViewModel.activePodcastViewData ?: return
// 2
        menuItem?.title = if (viewData.subscribed)
            getString(R.string.unsubscribe) else
            getString(R.string.subscribe)
    }


}

interface OnPodcastDetailsListener {
    fun onSubscribe()
    fun onUnsubscribe()
}