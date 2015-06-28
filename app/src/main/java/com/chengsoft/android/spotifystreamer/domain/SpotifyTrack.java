package com.chengsoft.android.spotifystreamer.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Track class for Spotify API
 */
public class SpotifyTrack implements Parcelable {

    private String trackName;
    private String albumName;
    private String largeThumbnailUrl;
    private String smallThumbnailUrl;
    private String trackPreviewUrl;

    /**
     * Constructor for {@link SpotifyTrack}
     * 
     * @param trackName the name of the track
     * @param albumName the name of the album
     * @param largeThumbnailUrl the thumbnail used for preview
     * @param smallThumbnailUrl the thumbnail used for list view
     * @param trackPreviewUrl the track preview url
     */
    public SpotifyTrack(String trackName,
                        String albumName,
                        String largeThumbnailUrl,
                        String smallThumbnailUrl,
                        String trackPreviewUrl) {
        init(trackName, albumName, largeThumbnailUrl, smallThumbnailUrl, trackPreviewUrl);
    }

    /**
     * * Create a common init() method so it can be shared with the {@link Parcelable} constructor
     * 
     * @param trackName the name of the track
     * @param albumName the name of the album
     * @param largeThumbnailUrl the thumbnail used for preview
     * @param smallThumbnailUrl the thumbnail used for list view
     * @param trackPreviewUrl the track preview url
     */
    private void init(String trackName, 
                      String albumName, 
                      String largeThumbnailUrl, 
                      String smallThumbnailUrl, 
                      String trackPreviewUrl) {
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

    // Implement Parcelable
    private SpotifyTrack(Parcel in) {
        String[] initArgs = in.createStringArray();
        init(initArgs[0],
                initArgs[1],
                initArgs[2],
                initArgs[3],
                initArgs[4]);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]
                {trackName,
                    albumName,
                    largeThumbnailUrl,
                    smallThumbnailUrl,
                    trackPreviewUrl});
    }

    public static final Parcelable.Creator<SpotifyTrack> CREATOR
            = new Parcelable.Creator<SpotifyTrack>() {
        public SpotifyTrack createFromParcel(Parcel in) {
            return new SpotifyTrack(in);
        }
        public SpotifyTrack[] newArray(int size) {
            return new SpotifyTrack[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
