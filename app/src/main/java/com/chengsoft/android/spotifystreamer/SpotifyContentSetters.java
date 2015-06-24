package com.chengsoft.android.spotifystreamer;

import com.chengsoft.android.spotifystreamer.domain.SpotifyArtist;
import com.chengsoft.android.spotifystreamer.domain.SpotifyTrack;
import com.chengsoft.android.spotifystreamer.support.BeanAdapter;
import com.chengsoft.android.spotifystreamer.support.ImageViewContentSetter;
import com.chengsoft.android.spotifystreamer.support.TextViewContentSetter;
import com.chengsoft.android.spotifystreamer.support.ViewContentSetter;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link ViewContentSetter}s for the Spotify API
 */
public class SpotifyContentSetters {

    /**
     * Convenience method to return the {@link ViewContentSetter} map required
     * for a {@link BeanAdapter} with type {@link SpotifyArtist}
     *
     * @return the content setter map
     */
    public static Map<Integer, ViewContentSetter<SpotifyArtist>> artistContentSetterMap() {
        Map<Integer, ViewContentSetter<SpotifyArtist>> contentSetterMap = new HashMap<>();
        contentSetterMap.put(R.id.list_item_artist_name,
                new TextViewContentSetter<SpotifyArtist>("name"));
        contentSetterMap.put(R.id.list_item_artist_thumbnail,
               new ImageViewContentSetter<SpotifyArtist>("thumbnailUrl", R.drawable.spotify));

        return contentSetterMap;
    }

    /**
     * Convenience method to return the {@link ViewContentSetter} map required
     * for a {@link BeanAdapter} with type {@link SpotifyTrack}
     *
     * @return the content setter map
     */
    public static Map<Integer, ViewContentSetter<SpotifyTrack>> topTracksContentSetterMap() {
        Map<Integer, ViewContentSetter<SpotifyTrack>> contentSetterMap = new HashMap<>();
        contentSetterMap.put(R.id.list_item_top_tracks_album,
                new TextViewContentSetter<SpotifyTrack>("albumName"));
        contentSetterMap.put(R.id.list_item_top_tracks_track,
                new TextViewContentSetter<SpotifyTrack>("trackName"));
        contentSetterMap.put(R.id.list_item_top_tracks_thumbnail,
                new ImageViewContentSetter<SpotifyTrack>("smallThumbnailUrl", R.drawable.spotify));

        return contentSetterMap;
    }
}
