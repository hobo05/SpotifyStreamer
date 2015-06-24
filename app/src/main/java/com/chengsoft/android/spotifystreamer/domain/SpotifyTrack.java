package com.chengsoft.android.spotifystreamer.domain;

/**
 * Track class for Spotify API
 */
public class SpotifyTrack {

    private String trackName;
    private String albumName;
    private String largeThumbnailUrl;
    private String smallThumbnailUrl;
    private String trackPreviewUrl;

    public SpotifyTrack(String trackName, String albumName, String largeThumbnailUrl, String smallThumbnailUrl, String trackPreviewUrl) {
        this.trackName = trackName;
        this.albumName = albumName;
        this.largeThumbnailUrl = largeThumbnailUrl;
        this.smallThumbnailUrl = smallThumbnailUrl;
        this.trackPreviewUrl = trackPreviewUrl;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getLargeThumbnailUrl() {
        return largeThumbnailUrl;
    }

    public void setLargeThumbnailUrl(String largeThumbnailUrl) {
        this.largeThumbnailUrl = largeThumbnailUrl;
    }

    public String getSmallThumbnailUrl() {
        return smallThumbnailUrl;
    }

    public void setSmallThumbnailUrl(String smallThumbnailUrl) {
        this.smallThumbnailUrl = smallThumbnailUrl;
    }

    public String getTrackPreviewUrl() {
        return trackPreviewUrl;
    }

    public void setTrackPreviewUrl(String trackPreviewUrl) {
        this.trackPreviewUrl = trackPreviewUrl;
    }
}
