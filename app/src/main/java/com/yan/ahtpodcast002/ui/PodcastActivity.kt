//https://itunes.apple.com/search?term=Android+Developer&media=podcast

package com.yan.ahtpodcast002.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.threetenabp.AndroidThreeTen
import com.yan.ahtpodcast002.R
import com.yan.ahtpodcast002.repository.ItunesRepository
import com.yan.ahtpodcast002.repository.PodcastRepository
import com.yan.ahtpodcast002.service.FeedService
import com.yan.ahtpodcast002.service.ItunesService
import com.yan.ahtpodcast002.ui.adapter.PodcastListAdapter
import com.yan.ahtpodcast002.ui.adapter.PodcastListAdapterListener
import com.yan.ahtpodcast002.viewmodels.PodcastViewModel
import com.yan.ahtpodcast002.viewmodels.SearchViewModel
import kotlinx.android.synthetic.main.activity_podcast.*

class PodcastActivity : AppCompatActivity(), PodcastListAdapterListener {

    private val TAG = javaClass.simpleName

    private val searchViewModel by viewModels<SearchViewModel>()
    private lateinit var podcastListAdapter: PodcastListAdapter
    private lateinit var searchMenuItem: MenuItem
    private val podcastViewModel by viewModels<PodcastViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_podcast)
        AndroidThreeTen.init(this)

//        val itunesService = ItunesService.instance
//        val itunesRepository = ItunesRepository(itunesService)
//        itunesRepository.searchByTerm("Android Developer"){
//            Log.d(TAG,"Results = $it")
//        }
        setupToolbar()

        setupViewModels()
        updateControls()
        // This get the saved Intent and passes it to the existing handleIntent() method
        handleIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //1
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)
        if (menu != null) {
            searchMenuItem = menu.findItem(R.id.search_item)
        }
        val searchView = searchMenuItem.actionView as SearchView
        //3
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        //4
        searchView.setSearchableInfo(

            searchManager.getSearchableInfo(componentName)
        )

        if (podcastRecyclerView.visibility == View.INVISIBLE) {
            searchMenuItem.isVisible = false
        }
        return true
    }

    private fun performSearch(term: String) {
        showProgressBar()

        searchViewModel.searchPodcasts(term) { results ->
            hideProgressBar()
            toolbar.title = term
            podcastListAdapter.setSearchData(results)
        }
    }

    private fun handleIntent(intent: Intent) {

        if (Intent.ACTION_SEARCH == intent.action) {

            val query = intent.getStringExtra(SearchManager.QUERY) ?: return
            performSearch(query)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent != null) {
            handleIntent(intent)
        }

    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
    }

    // setup recyclerview with adapter
    private fun setupViewModels() {

        val service = ItunesService.instance
        val rssService = FeedService.instance
        searchViewModel.itunesRepository = ItunesRepository(service)
        podcastViewModel.podcastRepository = PodcastRepository(rssService)

    }

    private fun updateControls() {
        podcastRecyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        podcastRecyclerView.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(
            podcastRecyclerView.context,
            layoutManager.orientation
        )
        podcastRecyclerView.addItemDecoration(dividerItemDecoration)
        podcastListAdapter = PodcastListAdapter(null, this, this)
        podcastRecyclerView.adapter = podcastListAdapter
    }

    // Interface onclick
    override fun onShowDetails(podcastSummaryViewData: SearchViewModel.PodcastSummaryViewData) {
//        TODO("Not yet implemented")

        val feedUrl = podcastSummaryViewData.feedUrl ?: return
        showProgressBar()
        podcastViewModel.getPodcast(podcastSummaryViewData) {

            hideProgressBar()
            if (it != null) {
                showDetailsFragment()
            } else {
                showError("Error loading feed $feedUrl")
            }

        }

    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

    //
    companion object {
        private const val TAG_DETAILS_FRAGMENT = "DetailsFragment"
    }

    /**Create Fragments*/

    private fun createPodcastDetailsFragment():

            PodcastDetailsFragment {
        var podcastDetailsFragment = supportFragmentManager.findFragmentByTag(
            TAG_DETAILS_FRAGMENT
        ) as PodcastDetailsFragment?

        if (podcastDetailsFragment == null) {
            podcastDetailsFragment = PodcastDetailsFragment.newInstance()
        }
        return podcastDetailsFragment
    }

    // display details Fragment

    private fun showDetailsFragment() {
// 1
        val podcastDetailsFragment = createPodcastDetailsFragment()
// 2
        supportFragmentManager.beginTransaction().add(
            R.id.podcastDetailsContainer,
            podcastDetailsFragment, PodcastActivity.TAG_DETAILS_FRAGMENT
        )
            .addToBackStack("DetailsFragment").commit()
// 3
        podcastRecyclerView.visibility = View.INVISIBLE
// 4
        searchMenuItem.isVisible = false
    }

    private fun showError(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok_button), null)
            .create()
            .show()
    }

}