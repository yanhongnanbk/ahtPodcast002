package com.yan.ahtpodcast002.database

import android.content.Context
import androidx.room.*
import com.yan.ahtpodcast002.entities.models.Episode
import com.yan.ahtpodcast002.entities.models.Podcast
import java.util.*

// 1
@Database(
    entities = arrayOf(Podcast::class, Episode::class),
    version = 1
)
@TypeConverters(Converters::class)
abstract class PodPlayDatabase : RoomDatabase() {
    // 2
    abstract fun podcastDao(): PodcastDao

    // 3
    companion object {
        // 4
        private var instance: PodPlayDatabase? = null

        // 5
        fun getInstance(context: Context): PodPlayDatabase {
            if (instance == null) {
// 6
                instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        PodPlayDatabase::class.java, "PodPlayer"
                    ).build()
            }
// 7
            return instance as PodPlayDatabase
        }
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return (date?.time)
    }
}