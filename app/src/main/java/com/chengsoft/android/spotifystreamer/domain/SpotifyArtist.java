package com.chengsoft.android.spotifystreamer.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Artist class for Spotify API
 */
public class SpotifyArtist implements Parcelable {
    private String id;
    private String name;
    private String thumbnailUrl;

    public SpotifyArtist(String id, String name, String thumbnailUrl) {
        init(id, name, thumbnailUrl);
    }

    /**
     * Create a common init() method so it can be shared with the {@link Parcelable} constructor
     *
     * @param id the artist id
     * @param name the name of the artist
     * @param thumbnailUrl the thumbnail url
     */
    private void init(String id, String name, String thumbnailUrl) {
        this.id = id;
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    // Implement Parcelable
    private SpotifyArtist(Parcel in) {
        String[] initArgs = in.createStringArray();
        init(initArgs[0], initArgs[1], initArgs[2]);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{id, name, thumbnailUrl});
    }

    public static final Parcelable.Creator<SpotifyArtist> CREATOR
            = new Parcelable.Creator<SpotifyArtist>() {
        public SpotifyArtist createFromParcel(Parcel in) {
            return new SpotifyArtist(in);
        }
        public SpotifyArtist[] newArray(int size) {
            return new SpotifyArtist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
