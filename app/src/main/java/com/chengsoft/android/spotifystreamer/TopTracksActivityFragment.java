package com.chengsoft.android.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {

    private ArrayAdapter<String> mTopTracksAdapter;

    public TopTracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        // Create an ArrayAdapter that will act as the datasource of the ListView
        ArrayList<String> trackNames = new ArrayList<>();
        trackNames.add("Track A");
        trackNames.add("Track B");

        mTopTracksAdapter = new ArrayAdapter<String>(
                // The current context, the fragment's parent activity
                getActivity(),
                // ID of the list item layout
                R.layout.list_item_top_tracks,
                // ID of the TextView to populate
                R.id.list_item_top_tracks_track,
                // Forecast data
                trackNames);

        // Find list view by using its ID inside the rootView
        ListView listViewTopTracks = (ListView) rootView.findViewById(R.id.listview_top_tracks);

        // Set the adapter to the dummy data
        listViewTopTracks.setAdapter(mTopTracksAdapter);

        // Retrieve the EXTRA_TEXT from the intent and set it
        // to the text view
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            // Retrieve the artist and set it to the detail view
            String artist = intent.getStringExtra(Intent.EXTRA_TEXT);
            Toast toast = Toast.makeText(getActivity(), artist, Toast.LENGTH_SHORT);
            toast.show();
        }

        return rootView;
    }
}
