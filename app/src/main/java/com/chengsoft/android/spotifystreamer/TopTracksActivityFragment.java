package com.chengsoft.android.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chengsoft.android.spotifystreamer.domain.SpotifyTrack;
import com.chengsoft.android.spotifystreamer.support.BeanAdapter;
import com.chengsoft.android.spotifystreamer.support.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {

    private BeanAdapter<SpotifyTrack> topTracksAdapter;
    private ListView listViewTopTracks;
    private ProgressBar topTracksProgressBar;
    private Integer mCrossfadeDuration;

    // Store the last ten results
    private LruCache<String, List<SpotifyTrack>> topTracksCache = new LruCache<>(10);

    // Used to save/restore state
    private static final String STATEFUL_TOP_TRACKS_SEARCH_BUNDLE = "topTracksSearchBundle";
    private static final String STATEFUL_SPOTIFY_TRACK_LIST = "spotifyTrackList";
    private Bundle extraTopTracksSearchBundle;

    private final String LOG_TAG = TopTracksActivityFragment.class.getSimpleName();

    public TopTracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        // Find list view by using its ID inside the rootView
        listViewTopTracks = (ListView) rootView.findViewById(R.id.listview_top_tracks);
        // Find progress bar
        topTracksProgressBar = (ProgressBar) rootView.findViewById(R.id.top_tracks_progressBar);

        // Retrieve and cache the system's default "medium" animation time.
        mCrossfadeDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);

        // Create adapter
        topTracksAdapter = new BeanAdapter<SpotifyTrack>(
                // The current context, the fragment's parent activity
                getActivity(),
                // ID of the list item layout
                R.layout.list_item_top_tracks,
                // track data
                new ArrayList<SpotifyTrack>(),
                // content setter map
                SpotifyContentSetters.topTracksContentSetterMap());

        // Set the adapter to the ListView
        listViewTopTracks.setAdapter(topTracksAdapter);

        // Only handle the intent if it includes the EXTRA_TOP_TRACKS_BUNDLE
        // and the list items are NOT saved
        // The reason being, if we have the list items cached already, we
        // don't need to kick off a SearchTopTracksTask
        Boolean listItemSaved = savedInstanceState != null
                && savedInstanceState.containsKey(STATEFUL_TOP_TRACKS_SEARCH_BUNDLE);
        Intent intent = getActivity().getIntent();
        if (intent != null
                && intent.hasExtra(SearchActivity.EXTRA_TOP_TRACKS_BUNDLE)
                && !listItemSaved) {
            extraTopTracksSearchBundle = intent.getBundleExtra(SearchActivity.EXTRA_TOP_TRACKS_BUNDLE);
            String artistName = extraTopTracksSearchBundle.getString(SearchActivity.EXTRA_ARTIST_NAME);
            String artistId = extraTopTracksSearchBundle.getString(SearchActivity.EXTRA_ARTIST_ID);

            // Set subtitle of activity's actionbar
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            actionBar.setSubtitle(artistName);

            // Retrieve the artist id and use it to retrieve the top tracks
            new SearchTopTracksTask(artistId, 640, 300).execute();
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save top tracks bundle
        outState.putBundle(STATEFUL_TOP_TRACKS_SEARCH_BUNDLE, extraTopTracksSearchBundle);

        // Save list items
        ArrayList<SpotifyTrack> spotifyTrackList = new ArrayList<>(topTracksAdapter.getValues());
        outState.putParcelableArrayList(STATEFUL_TOP_TRACKS_SEARCH_BUNDLE, spotifyTrackList);

        // Save view hierarchy
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // restore view hierarchy
        super.onActivityCreated(savedInstanceState);

        // restore top tracks bundle and list items
        if (savedInstanceState != null) {
            extraTopTracksSearchBundle = savedInstanceState.getBundle(STATEFUL_TOP_TRACKS_SEARCH_BUNDLE);
            ArrayList<SpotifyTrack> spotifyTrackList = savedInstanceState.getParcelableArrayList(STATEFUL_TOP_TRACKS_SEARCH_BUNDLE);
            topTracksAdapter.replaceAll(spotifyTrackList);
        }
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

    private class SearchTopTracksTask extends AsyncTask<Void, Void, List<SpotifyTrack>> {

        private String artistId;
        private Integer largePreferredWidth;
        private Integer smallPreferredWidth;
        private final String LOG_TAG = SearchTopTracksTask.class.getSimpleName();

        /**
         * Constructor for {@link com.chengsoft.android.spotifystreamer.TopTracksActivityFragment.SearchTopTracksTask}
         *
         * @param artistId the artist id
         * @param largePreferredWidth the preferred width for the preview track thumbnail
         * @param smallPreferredWidth the preferred width for the list view thumbnail
         */
        public SearchTopTracksTask(String artistId, Integer largePreferredWidth, Integer smallPreferredWidth) {
            this.artistId = artistId;
            this.largePreferredWidth = largePreferredWidth;
            this.smallPreferredWidth = smallPreferredWidth;
        }

        @Override
        protected void onPreExecute() {
            // When searching hide the results and show the progressbar
            ViewUtils.swap(topTracksProgressBar, listViewTopTracks);
        }

        @Override
        protected List<SpotifyTrack> doInBackground(Void... params) {
            // Check if the top tracks are in the cache, return if so
            List<SpotifyTrack> artistTopTracks = topTracksCache.get(artistId);
            if (artistTopTracks != null) {
                return artistTopTracks;
            }

            // Otherwise call the API to retrieve the top tracks
            SpotifyApi api = new SpotifyApi();
            SpotifyService service = api.getService();
            // TODO Hard-code country code for now
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("country", "US");
            Tracks wrapperArtistTopTracks = service.getArtistTopTrack(artistId, queryMap);
            List<Track> apiTracks = wrapperArtistTopTracks.tracks;

            // Convert the tracks to SpotifyTracks and return it
            artistTopTracks = new ArrayList<>();
            if (apiTracks != null) {
                // Process the tracks and put it in the cache
                artistTopTracks = processTracks(apiTracks);
                topTracksCache.put(artistId, artistTopTracks);
            }
            return artistTopTracks;
        }

        @Override
        protected void onPostExecute(List<SpotifyTrack> artistTopTracks) {
            // Display a message if can't find top tracks
            if (artistTopTracks.isEmpty()) {
                // Immediately show no results
                ViewUtils.swap(listViewTopTracks, topTracksProgressBar);
                makeToast(getString(R.string.no_top_tracks_found));
                return;
            }

            // Set the bean adapter
            topTracksAdapter.clear();
            topTracksAdapter.addAll(artistTopTracks);

            // After call for tracks is done, hide the progress bar and show the results
            ViewUtils.crossfade(listViewTopTracks, topTracksProgressBar, mCrossfadeDuration);

        }

        /**
         * Convert the {@link Track}s from the API into our domain class {@link SpotifyTrack}
         *
         * @param artistTopTracks Tracks retrieved from the Spotify API
         * @return list of {@link SpotifyTrack}
         */
        private List<SpotifyTrack> processTracks(List<Track> artistTopTracks) {
            // Convert the tracks to SpotifyTracks
            List<SpotifyTrack> spotifyTracks = new ArrayList<>();
            for (Track curTrack : artistTopTracks) {
                // Find the appropriate thumbnail if any
                String largeThumbnailUrl = null;
                String smallThumbnailUrl = null;
                // Sort the images by descending width first
                Collections.sort(curTrack.album.images, new Comparator<Image>() {
                    @Override
                    public int compare(Image lhs, Image rhs) {
                        // Sort by descending width
                        return rhs.width - lhs.width;
                    }
                });
                // Find the preferred thumbnails
                for (Image curImage : curTrack.album.images) {
                    if (largeThumbnailUrl == null
                            && curImage.width <= largePreferredWidth) {
                        largeThumbnailUrl = curImage.url;
                    }
                    if (smallThumbnailUrl == null
                            && curImage.width <= smallPreferredWidth) {
                        smallThumbnailUrl = curImage.url;
                    }
                }

                // Create new SpotifyTrack
                SpotifyTrack newTrack = new SpotifyTrack(curTrack.name,
                        curTrack.album.name,
                        largeThumbnailUrl,
                        smallThumbnailUrl,
                        curTrack.preview_url);
                spotifyTracks.add(newTrack);
            }
            return spotifyTracks;
        }
    }
}
