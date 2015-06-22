package com.chengsoft.android.spotifystreamer;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * {@link ViewContentSetter}s for the Spotify API
 */
public class SpotifyContentSetters {

    public static ViewContentSetter<View, SpotifyArtist> artistNameContentSetter() {
        return new ArtistNameContentSetter();
    }

    public static ViewContentSetter<View, SpotifyArtist> artistThumbnailContentSetter(int defaultImageResource) {
        return new ArtistThumbnailContentSetter(defaultImageResource);
    }

    /**
     * Content setter for {@link SpotifyArtist} name
     */
    private static class ArtistNameContentSetter implements ViewContentSetter<View, SpotifyArtist> {
        @Override
        public void setViewContent(View view, SpotifyArtist artist) {
            TextView textView = (TextView) view;
            textView.setText(artist.getName());
        }
    }

    /**
     * Content setter for {@link SpotifyArtist} thumbnail
     */
    private static class ArtistThumbnailContentSetter implements ViewContentSetter<View, SpotifyArtist> {

        private int defaultImageResource;

        public ArtistThumbnailContentSetter(int defaultImageResource) {
            this.defaultImageResource = defaultImageResource;
        }

        @Override
        public void setViewContent(View view, SpotifyArtist artist) {
            ImageView imageView = (ImageView) view;

            // Only set thumbnail if the url was set
            if (artist.getThumbnailUrl() != null) {
                Picasso.with(imageView.getContext()).load(artist.getThumbnailUrl()).into(imageView);
            } else {
                // Set the default if nothing was found
                imageView.setImageResource(defaultImageResource);
            }
        }
    }

}
