package com.yan.ahtpodcast002.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yan.ahtpodcast002.R
import com.yan.ahtpodcast002.database.PodPlayDatabase
import com.yan.ahtpodcast002.repository.PodcastRepository
import com.yan.ahtpodcast002.service.FeedService
import com.yan.ahtpodcast002.ui.PodcastActivity
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class EpisodeUpdateWorker(
    context: Context,
    params: WorkerParameters
) :
    CoroutineWorker(context, params) {
    //    Where you perform the episode updating logic. Upon completion, must call Result.success(), Result.failure(),Result.retry(),... to indicate the job is finished
//    This is suspending function, can be called inside coroutine// call other suspending functions
//    implement onWork() with the update logic and trigger the notification
    // 1
    override suspend fun doWork(): Result = coroutineScope {
// 2    async => define a coroutine to run the update process in the background-> similar to GlobalScope.launch, difference: return a job
        val job = async {
// 3
            val db = PodPlayDatabase.getInstance(applicationContext)
            val repo = PodcastRepository(
                FeedService.instance,
                db.podcastDao()
            )
// 4    call this method to update the podcast episodes in background thread
            repo.updatePodcastEpisodes { podcastUpdates ->
// 5
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel()
                }
// 6    call displayNotification for each updated podcast
                for (podcastUpdate in podcastUpdates) {
                    displayNotification(podcastUpdate)
                }
            }
        }
// 7    suspend the job until the code inside the job is completed
        job.await()
// 8    After all the podcasts have been processed, call Result.success() to let the work manager know that the job is completed

        Result.success()
    }

    //1   Notify the compiler that this method should be called only when running on API level 26 or newer
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
// 2    The notificationManager is retrieved using ...getSystemService() is provided by the CoroutineWorkerClass
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
// 3    Check whether the channel already exists, if not -> create the channel
        if (notificationManager.getNotificationChannel(EPISODE_CHANNEL_ID) == null) {// 4
            val channel = NotificationChannel(
                EPISODE_CHANNEL_ID,
                "Episodes", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
//  Display Notification

    private fun displayNotification(podcastInfo: PodcastRepository.PodcastUpdateInfo) {
// 1    The application need to know which contents should be displayed when the user tap the notification,=> Provide a pendingContentIntent which points to the PodcastActivity, the feedUrl is used to display the podcast details screen
        val contentIntent = Intent(applicationContext, PodcastActivity::class.java)
        contentIntent.putExtra(EXTRA_FEED_URL, podcastInfo.feedUrl)
        val pendingContentIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
// 2
        val notification = NotificationCompat.Builder(
            applicationContext,
            EPISODE_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_episode_icon)
            .setContentTitle(
                applicationContext.getString(
                    R.string.episode_notification_title
                )
            )
            .setContentText(
                applicationContext.getString(
                    R.string.episode_notification_text,
                    podcastInfo.newCount, podcastInfo.name
                )
            )
            .setNumber(podcastInfo.newCount)
            .setAutoCancel(true)
            .setContentIntent(pendingContentIntent)
            .build()
// 4    The notification manager is retrieved using the getSystemService. The notification manager is instructed to notify the user with the notification object created by the builder
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE)
                    as NotificationManager
// 5    If notify() is called multiple times with the same tag and id, it will replace any existing noti with the same tag and id
        notificationManager.notify(podcastInfo.name, 0, notification)
    }

    companion object {
        const val EPISODE_CHANNEL_ID = "podplay_episodes_channel"
        const val EXTRA_FEED_URL = "PodcastFeedUrl"
    }
}