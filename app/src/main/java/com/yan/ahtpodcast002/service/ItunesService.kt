package com.yan.ahtpodcast002.service

import com.yan.ahtpodcast002.entities.remote.PodcastResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
const val BASE_URL = "https://itunes.apple.com"
interface ItunesService {
    // The companion object return an instance of the interface as a singleton. This ensures that the interface is only instantiated once during the app's life time
    //1
    @GET("search?media=podcast")
    //2
    fun searchPodcastByTerm(@Query("term") term: String): Call<PodcastResponse>
    //3
    companion object {
        //4 The result of using lazy method is that the first time the instance property is accessed, it executes the lambda and stores the result. All subsequent calls to the instance property return the original result
        val instance:ItunesService by lazy {
            //5
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            //6
            retrofit.create<ItunesService>(ItunesService::class.java)
        }

    }
}