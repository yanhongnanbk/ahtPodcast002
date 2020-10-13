package com.yan.ahtpodcast002.service

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.yan.ahtpodcast002.entities.rss.RssFeedResponse
import com.yan.ahtpodcast002.utils.DateUtils
import okhttp3.*
import org.w3c.dom.Node
import java.io.IOException
import javax.xml.parsers.DocumentBuilderFactory
import java.time.*
import java.time.format.DateTimeFormatter

class RssFeedService : FeedService {
    override fun getFeed(xmlFileURL: String, callback: (RssFeedResponse?) -> Unit) {

        //1 Create a new instance of OKHTTP client. Use this client to fetch data async => ensure the main thread not being blocked when fetching data
        val client = OkHttpClient()
        //2 Build object using the URL of the Rss
        val request = Request.Builder()
            .url(xmlFileURL)
            .build()

        //3 Pass the request to the client throw the newCall() => return a Call object
        // The call object enqueue method async executes the Request. Pass a callback object to enqueue(). When the request is completed, OkHttp calls either onFailure or onResponse
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
//                TODO("Not yet implemented")
                Log.d("RssFeedService","onFailure")

                callback(null) // indicate a failure
            }

            override fun onResponse(call: Call, response: Response) {
//                TODO("Not yet implemented")

                if (response.isSuccessful) {
// the response contains all the details about the returned obj, including the HTTP status code and the main response body
                    response.body()?.let { responseBody ->
//                        println(responseBody.string());
                        // parsing xml file to DOM. get the doc object
                        val dbFactory = DocumentBuilderFactory.newInstance()
                        val dbBuilder = dbFactory.newDocumentBuilder()
                        val doc = dbBuilder.parse(responseBody.byteStream())
                        //

                        val rssFeedResponse = RssFeedResponse(episodes = mutableListOf())
                        domToRssFeedResponse(doc,rssFeedResponse)
                        callback(rssFeedResponse)
                        println(rssFeedResponse)
                        return
                    }
//                    Log.d("RSSFeedResponse","${response.body().}")

                }
                println("no response")
                callback(null)
            }
        })
    }

    /**Dom To RssFeed*/
    private fun domToRssFeedResponse(node: Node, rssFeedResponse: RssFeedResponse) {

        //1 Check to make sure it is an XML element
        if (node.nodeType == Node.ELEMENT_NODE) {
            //2 Each note except the parent one contains a parent node. use the name of the parent note to determine where the current node resides in the tree
            val nodeName = node.nodeName
            val parentName = node.parentNode.nodeName

            val grandParentName = node.parentNode.parentNode?.nodeName?: ""
            // if this node is a child of an item node, and the item node is a child of a channel node, then it is an episode element
            if (parentName == "item" && grandParentName=="channel"){

                val currentItem = rssFeedResponse.episodes?.last()
                if (currentItem !=null){
                    when(nodeName){

                        "title"-> currentItem.title =node.textContent
                        "description"-> currentItem.description =node.textContent
                        "itunes:duration"-> currentItem.duration =node.textContent
                        "guid"-> currentItem.guid =node.textContent
                        "pubDate"-> currentItem.pubDate =node.textContent
                        "link"-> currentItem.link =node.textContent
                        "enclosure"-> {
                            currentItem.url =node.attributes.getNamedItem("url").textContent
                            currentItem.type =node.attributes.getNamedItem("type").textContent
                        }
                    }
                }

            }


            //3 if the current node is a child of the channel node, extract the top level RSS feed info from this node
            if (parentName == "channel"){

                //4 depending on the nodename, you fill in the top level rssFeedResponse data with the textContent of the node. If the node is an episode item, you add a new empty EpisodeResponse object to the episodeList
                when(nodeName){
                    "title"-> rssFeedResponse.title = node.textContent
                    "description" ->rssFeedResponse.description = node.textContent
                    "itunes:summary" ->rssFeedResponse.summary = node.textContent
                    "item" ->rssFeedResponse.episodes?.add(RssFeedResponse.EpisodeResponse())

                    "pubDate" -> {

                        val x:String = node.textContent
                        rssFeedResponse.lastUpdated = DateUtils.xmlToDate(x)}
                }
            }
        }
            //5 assign nodeList  to the list of child nodes for the currentnode
        val nodeList = node.childNodes
        // 6 for each childnode, call domtoRssFeedResponse, passing in the existing rssFeedResponseObject => allow domToRssFeedResponse object in recursive fashion
        for (i in 0 until nodeList.length){
            val childNode = nodeList.item(i)
            domToRssFeedResponse(childNode,rssFeedResponse)
        }

    }

}

interface FeedService {
    //1 getFeed take URL pointing to an RSS file and a callback method. After the file is loaded and parsed, the callback method get called with the final RSS feed response
    fun getFeed(xmlFileURL: String, callback: (RssFeedResponse?) -> Unit)

    //2 => Provide a singleton instance of FeedService
    companion object {
        val instance: FeedService by lazy {
            RssFeedService()
        }
    }
}
