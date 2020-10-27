package com.yan.ahtpodcast002.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.yan.ahtpodcast002.entities.models.Episode
import com.yan.ahtpodcast002.entities.models.Podcast

// 1
@Dao
interface PodcastDao {
    // 2
    @Query("SELECT * FROM Podcast ORDER BY FeedTitle")
    fun loadPodcasts(): LiveData<List<Podcast>>

    // 3
    @Query("SELECT * FROM Episode WHERE podcastId = :podcastId ORDER BY releaseDate DESC")
    fun loadEpisodes(podcastId: Long): List<Episode>

    // 4
    @Insert(onConflict = REPLACE)
    fun insertPodcast(podcast: Podcast): Long

    // 5
    @Insert(onConflict = REPLACE)
    fun insertEpisode(episode: Episode): Long

    @Query("SELECT * FROM Podcast WHERE feedUrl = :url")
    fun loadPodcast(url: String): Podcast?

    @Delete
    fun deletePodcast(podcast: Podcast)

    @Query("SELECT * FROM Podcast ORDER BY FeedTitle")
    fun loadPodcastsStatic(): List<Podcast>
}

