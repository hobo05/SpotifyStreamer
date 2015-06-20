package com.chengsoft.android.spotifystreamer;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends Fragment {

    public SearchActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create an ArrayAdapter that will act as the datasource of the ListView
        ArrayList<String> artistNames = new ArrayList<>();
        artistNames.add("Artist A");
        artistNames.add("Artist B");

        ArrayAdapter mArtistAdapter = new ArrayAdapter<String>(
                // The current context, the fragment's parent activity
                getActivity(),
                // ID of the list item layout
                R.layout.list_item_artist,
                // ID of the TextView to populate
                R.id.list_item_artist_name,
                // Forecast data
                artistNames);

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        // Find list view by using its ID inside the rootView
        ListView listViewForecast = (ListView) rootView.findViewById(R.id.listview_artists);

        // Set the adapter to the dummy data
        listViewForecast.setAdapter(mArtistAdapter);

        return rootView;
    }
}
