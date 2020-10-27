package com.yan.ahtpodcast002.service

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat

class PodplayMediaService : MediaBrowserServiceCompat() {
    private lateinit var mediaSession: MediaSessionCompat
    override fun onCreate() {
        super.onCreate()
        createMediaSession()
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
// To be implemented
        if (parentId.equals(PODPLAY_EMPTY_ROOT_MEDIA_ID)) {
            result.sendResult(null)
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int, rootHints: Bundle?
    ): BrowserRoot? {
// To be implemented
        return MediaBrowserServiceCompat.BrowserRoot(PODPLAY_EMPTY_ROOT_MEDIA_ID, null)
    }

    private fun createMediaSession() {
// 1    Initialize the mediaSession
        mediaSession = MediaSessionCompat(this, "PodplayMediaService")
// 2    Unique token for the media session is retrieved, and applied as the session token on the PodplayMediaService, which link the service to the mediasession
        setSessionToken(mediaSession.sessionToken)
// 3
// Assign Callback
        // this create a new instance of PodplayMediaCallback and sets it as the media session callback
        val callBack = PodplayMediaCallback(this, mediaSession)
        mediaSession.setCallback(callBack)
    }

    companion object {
        private const val PODPLAY_EMPTY_ROOT_MEDIA_ID = "podplay_empty_root_media_id"
    }
}