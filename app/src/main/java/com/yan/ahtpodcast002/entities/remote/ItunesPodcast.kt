package com.yan.ahtpodcast002.entities.remote


import com.google.gson.annotations.SerializedName

data class ItunesPodcast(
    @SerializedName("artistName")
    val artistName: String,
    @SerializedName("artworkUrl100")
    val artworkUrl100: String,
    @SerializedName("artworkUrl30")
    val artworkUrl30: String,
    @SerializedName("artworkUrl60")
    val artworkUrl60: String,
    @SerializedName("artworkUrl600")
    val artworkUrl600: String,
    @SerializedName("collectionCensoredName")
    val collectionCensoredName: String,
    @SerializedName("collectionExplicitness")
    val collectionExplicitness: String,
    @SerializedName("collectionHdPrice")
    val collectionHdPrice: Int,
    @SerializedName("collectionId")
    val collectionId: Int,
    @SerializedName("collectionName")
    val collectionName: String,
    @SerializedName("collectionPrice")
    val collectionPrice: Double,
    @SerializedName("collectionViewUrl")
    val collectionViewUrl: String,
    @SerializedName("contentAdvisoryRating")
    val contentAdvisoryRating: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("feedUrl")
    val feedUrl: String,
    @SerializedName("genreIds")
    val genreIds: List<String>,
    @SerializedName("genres")
    val genres: List<String>,
    @SerializedName("kind")
    val kind: String,
    @SerializedName("primaryGenreName")
    val primaryGenreName: String,
    @SerializedName("releaseDate")
    val releaseDate: String,
    @SerializedName("trackCensoredName")
    val trackCensoredName: String,
    @SerializedName("trackCount")
    val trackCount: Int,
    @SerializedName("trackExplicitness")
    val trackExplicitness: String,
    @SerializedName("trackHdPrice")
    val trackHdPrice: Int,
    @SerializedName("trackHdRentalPrice")
    val trackHdRentalPrice: Int,
    @SerializedName("trackId")
    val trackId: Int,
    @SerializedName("trackName")
    val trackName: String,
    @SerializedName("trackPrice")
    val trackPrice: Double,
    @SerializedName("trackRentalPrice")
    val trackRentalPrice: Int,
    @SerializedName("trackViewUrl")
    val trackViewUrl: String,
    @SerializedName("wrapperType")
    val wrapperType: String
)