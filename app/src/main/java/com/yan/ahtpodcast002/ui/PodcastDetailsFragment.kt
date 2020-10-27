package com.yan.ahtpodcast002.ui

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.yan.ahtpodcast002.R
import com.yan.ahtpodcast002.service.PodplayMediaService
import com.yan.ahtpodcast002.ui.adapter.EpisodeListAdapter
import com.yan.ahtpodcast002.viewmodels.PodcastViewModel
import kotlinx.android.synthetic.main.fragment_podcast_details.*

class PodcastDetailsFragment : Fragment(), EpisodeListAdapter.EpisodeListAdapterListener {

    private val podcastViewModel: PodcastViewModel by activityViewModels()
    private lateinit var episodeListAdapter: EpisodeListAdapter
    private var listener: OnPodcastDetailsListener? = null
    private var menuItem: MenuItem? = null
    private lateinit var mediaBrowser: MediaBrowserCompat
    private var mediaControllerCallback: MediaControllerCallback? = null
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
        initMediaBrowser()
    }

    override fun onStart() {
        super.onStart()
        if (mediaBrowser.isConnected) {
            val fragmentActivity = activity as FragmentActivity
            if (MediaControllerCompat.getMediaController
                    (fragmentActivity) == null
            ) {
                registerMediaController(mediaBrowser.sessionToken)
            }
        } else {
            mediaBrowser.connect()
        }
    }

    override fun onStop() {
        super.onStop()
        val fragmentActivity = activity as FragmentActivity
        if (MediaControllerCompat.getMediaController(fragmentActivity) != null) {
            mediaControllerCallback?.let {
                MediaControllerCompat.getMediaController(fragmentActivity).unregisterCallback(it)
            }
        }
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
            podcastViewModel.activePodcastViewData?.episodes, this
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

    /**Chap26*/

    inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            println(
                "metadata changed to ${
                    metadata?.getString(
                        MediaMetadataCompat.METADATA_KEY_MEDIA_URI
                    )
                }"
            )
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            println("state changed to $state")
        }
    }

    private fun registerMediaController(
        token:
        MediaSessionCompat.Token
    ) {
// 1    Assign a local fragmentActivity since activity is a property that can change to null between calls
        val fragmentActivity = activity as FragmentActivity
// 2    Associate the mediacontroller with the token from the mediaSession object. =>connect mediaController with the mediaSession
        val mediaController = MediaControllerCompat(fragmentActivity, token)
// 3
        MediaControllerCompat.setMediaController(fragmentActivity, mediaController)
// 4    Create  a new instance of MediaControllerCallback and set it as the callback object for the media controller
        mediaControllerCallback = MediaControllerCallback()
        mediaController.registerCallback(mediaControllerCallback!!)
    }

    inner class MediaBrowserCallBacks : MediaBrowserCompat.ConnectionCallback() {
        // 1    This is called after a successful connection to the MediaBroweserService. or it call onConnectionFailed if there is an issue
        override fun onConnected() {
            super.onConnected()
// 2    The mediaController is registered
            registerMediaController(mediaBrowser.sessionToken)
            println("onConnected")
        }

        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
            println("onConnectionSuspended")
// Disable transport controls
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
            println("onConnectionFailed")
// Fatal error handling
        }
    }

    private fun initMediaBrowser() {
        val fragmentActivity = activity as FragmentActivity
        mediaBrowser = MediaBrowserCompat(
            fragmentActivity,
            ComponentName(
                fragmentActivity,
                PodplayMediaService::class.java
            ),
            MediaBrowserCallBacks(),
            null
        )
    }

    private fun startPlaying(episodeViewData: PodcastViewModel.EpisodeViewData) {
        val fragmentActivity = activity as FragmentActivity
        val controller =
            MediaControllerCompat.getMediaController(fragmentActivity)
        controller.transportControls.playFromUri(
            Uri.parse(episodeViewData.mediaUrl), null
        )
    }

    override fun onSelectedEpisode(episodeViewData: PodcastViewModel.EpisodeViewData) {
        // 1
        val fragmentActivity = activity as FragmentActivity
// 2    get the mediaController that was previously assigned to the Activity.
        val controller = MediaControllerCompat.getMediaController(fragmentActivity)
// 3    If the playback state is not null, then check the state
        if (controller.playbackState != null) {
            if (controller.playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
// 4
                controller.transportControls.pause()
            } else {
// 5
                startPlaying(episodeViewData)
            }
        } else {
// 6
            startPlaying(episodeViewData)
        }
    }
}

interface OnPodcastDetailsListener {
    fun onSubscribe()
    fun onUnsubscribe()
}