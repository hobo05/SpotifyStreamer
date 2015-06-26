package com.chengsoft.android.spotifystreamer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chengsoft.android.spotifystreamer.domain.SpotifyArtist;
import com.chengsoft.android.spotifystreamer.support.BeanAdapter;
import com.chengsoft.android.spotifystreamer.support.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;


public class SearchActivity extends AppCompatActivity {
    // Intent constant
    public static final String EXTRA_TOP_TRACKS_MAP = "searchTopTracksMap";
    public static final String EXTRA_ARTIST_NAME = "artistName";
    public static final String EXTRA_ARTIST_ID = "artistId";

    // We need a reference to the fragment so we can use it to search the artists
    private PlaceholderFragment searchFragment;

    public void setSearchFragment(PlaceholderFragment searchFragment) {
        this.searchFragment = searchFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        // Handle all the intents
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String artistName = intent.getStringExtra(SearchManager.QUERY);
            // Search for artist
            searchFragment.searchArtist(artistName);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);


        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {

        private final String LOG_TAG = PlaceholderFragment.class.getSimpleName();

        private BeanAdapter<SpotifyArtist> mArtistAdapter;
        private ProgressBar searchArtistProgressBar;
        private Integer mCrossfadeDuration;
        private ListView listViewArtist;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_search, container, false);

            // Find list view by using its ID inside the rootView
            listViewArtist = (ListView) rootView.findViewById(R.id.listview_artists);
            // find the progressbar
            searchArtistProgressBar = (ProgressBar) rootView.findViewById(R.id.search_artist_progressBar);

            // Retrieve and cache the system's default "medium" animation time.
            mCrossfadeDuration = getResources().getInteger(
                    android.R.integer.config_mediumAnimTime);

            // Create adapter
            mArtistAdapter = new BeanAdapter<>(
                    // The current context, the fragment's parent activity
                    getActivity(),
                    // ID of the list item layout
                    R.layout.list_item_artist,
                    // artist data
                    new ArrayList<SpotifyArtist>(),
                    // Content setter map
                    SpotifyContentSetters.artistContentSetterMap());

            // Set the adapter to the dummy data
            listViewArtist.setAdapter(mArtistAdapter);

            // Set click listener for items
            listViewArtist.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Create explicit intent to call the DetailActivity
                            Intent topTracksActivityIntent = new Intent(getActivity(), TopTracksActivity.class);
                            // Set the artist name and id into the map
                            SpotifyArtist artist = mArtistAdapter.getItem(position);
                            HashMap<String, String> extraTopTracksMap = new HashMap<String, String>();
                            extraTopTracksMap.put(EXTRA_ARTIST_NAME, artist.getName());
                            extraTopTracksMap.put(EXTRA_ARTIST_ID, artist.getId());
                            topTracksActivityIntent.putExtra(EXTRA_TOP_TRACKS_MAP, extraTopTracksMap);
                            // Start activity
                            startActivity(topTracksActivityIntent);
                        }
                    }
            );


            // Set the fragment into the activity
            SearchActivity searchActivity = (SearchActivity) getActivity();
            searchActivity.setSearchFragment(this);

            return rootView;
        }

        /**
         * Search for an artist and update the {@link ListView}
         * @param artistName The name of the artist to search for
         */
        public void searchArtist(String artistName) {
            new SearchArtistTask(artistName, 300).execute();
        }

        /**
         * Show a toast for a short time
         *
         * @param message the message to display
         */
        private void makeToast(String message) {
            Toast toast = Toast.makeText(
                    getActivity(),
                    message,
                    Toast.LENGTH_SHORT);
            toast.show();
        }

        private class SearchArtistTask extends AsyncTask<Void, Void, List<SpotifyArtist>> {

            private final String LOG_TAG = SearchArtistTask.class.getSimpleName();
            private String artistName;
            private Integer preferredThumbnailWidth;

            public SearchArtistTask(String artistName, Integer preferredThumbnailWidth) {
                this.artistName = artistName;
                this.preferredThumbnailWidth = preferredThumbnailWidth;
            }

            @Override
            protected void onPreExecute() {
                // When searching hide the results and show the progressbar
                ViewUtils.swap(searchArtistProgressBar, listViewArtist);
            }

            @Override
            protected List<SpotifyArtist> doInBackground(Void... params) {

                SpotifyApi api = new SpotifyApi();
                SpotifyService service = api.getService();
                ArtistsPager pager = service.searchArtists(artistName);
                List<Artist> artists = pager.artists.items;

                List<SpotifyArtist> spotifyArtists = new ArrayList<>();
                if (artists != null) {
                    spotifyArtists = processArtists(artists);
                }

                return spotifyArtists;
            }

            @Override
            protected void onPostExecute(List<SpotifyArtist> artists) {
                // Display a toast to let user know if no artists were found
                if (artists.isEmpty()) {
                    // Immediately show no results
                    ViewUtils.swap(listViewArtist, searchArtistProgressBar);
                    makeToast(getString(R.string.no_artists_found));
                    return;
                }

                // Set the new artists in the adapter
                mArtistAdapter.clear();
                mArtistAdapter.addAll(artists);

                // After the search is done, hide the progress bar and show the results
                ViewUtils.crossfade(listViewArtist, searchArtistProgressBar, mCrossfadeDuration);
            }

            /**
             * * Convert the {@link Artist}s from the API into our domain class {@link SpotifyArtist}
             *
             * @param artists the artists to process and convert
             * @return a list of {@link SpotifyArtist}s
             */
            private List<SpotifyArtist> processArtists(List<Artist> artists) {
                // Create list of artists from API
                List<SpotifyArtist> spotifyArtists = new ArrayList<>();
                for (Artist curArtist : artists) {
                    // Find the appropriate thumbnail if any
                    String thumbnailUrl = null;
                    // Sort the images by descending width first
                    Collections.sort(curArtist.images, new Comparator<Image>() {
                        @Override
                        public int compare(Image lhs, Image rhs) {
                            // Sort by descending width
                            return rhs.width - lhs.width;
                        }
                    });
                    for (Image curImage : curArtist.images) {

                        thumbnailUrl = curImage.url;
                        // If the width is already at the preferred width or less,
                        // stop looking and set the image
                        // Otherwise, the last picture given will be the one that
                        // will end up being the thumbnail
                        if (curImage.width <= preferredThumbnailWidth) {
                            break;
                        }
                    }
                    // Create a new spotify artist and add it to the list
                    SpotifyArtist newArtist = new SpotifyArtist(curArtist.id, curArtist.name, thumbnailUrl);
                    spotifyArtists.add(newArtist);
                }
                return spotifyArtists;
            }
        }
    }
}
