package com.chengsoft.android.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    private BeanAdapter<SpotifyTrack> mTopTracksAdapter;
    private ListView listViewTopTracks;
    private ProgressBar topTracksProgressBar;
    private Integer mCrossfadeDuration;

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
        mTopTracksAdapter = new BeanAdapter<SpotifyTrack>(
                // The current context, the fragment's parent activity
                getActivity(),
                // ID of the list item layout
                R.layout.list_item_top_tracks,
                // track data
                new ArrayList<SpotifyTrack>(),
                // content setter map
                SpotifyContentSetters.topTracksContentSetterMap());

        // Set the adapter to the ListView
        listViewTopTracks.setAdapter(mTopTracksAdapter);

        // Retrieve the EXTRA_TOP_TRACKS_MAP from the intent
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(SearchActivity.EXTRA_TOP_TRACKS_MAP)) {
            HashMap<String, String> extraTopTracksMap
                    = (HashMap<String, String>)intent.getSerializableExtra(
                                                SearchActivity.EXTRA_TOP_TRACKS_MAP);
            String artistName = extraTopTracksMap.get(SearchActivity.EXTRA_ARTIST_NAME);
            String artistId = extraTopTracksMap.get(SearchActivity.EXTRA_ARTIST_ID);

            // Set subtitle of activity's actionbar
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            actionBar.setSubtitle(artistName);

            // Retrieve the artist id and use it to retrieve the top tracks
            new SearchTopTracksTask(artistId, 640, 300).execute();
        }

        return rootView;
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

    private class SearchTopTracksTask extends AsyncTask<Void, Void, List<Track>> {

        private String artistId;
        private Integer largePreferredWidth;
        private Integer smallPreferredWidth;
        private final String LOG_TAG = SearchTopTracksTask.class.getSimpleName();

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
        protected List<Track> doInBackground(Void... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService service = api.getService();
            // TODO Hard-code country code for now
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("country", "US");
            Tracks artistTopTracks = service.getArtistTopTrack(artistId, queryMap);

            return artistTopTracks.tracks;
        }

        @Override
        protected void onPostExecute(List<Track> artistTopTracks) {
            // Display a message if can't find top tracks
            if (artistTopTracks == null || artistTopTracks.isEmpty()) {
                makeToast("Cannot find top tracks!");
                return;
            }

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

            // Set the bean adapter
            mTopTracksAdapter.clear();
            mTopTracksAdapter.addAll(spotifyTracks);

            // After call for tracks is done, hide the progress bar and show the results
            ViewUtils.crossfade(listViewTopTracks, topTracksProgressBar, mCrossfadeDuration);

        }
    }
}
