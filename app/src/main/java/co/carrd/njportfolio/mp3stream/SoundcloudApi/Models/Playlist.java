package co.carrd.njportfolio.mp3stream.SoundcloudApi.Models;

import android.os.Parcel;
import android.os.Parcelable;

import co.carrd.njportfolio.mp3stream.SoundcloudApi.ApiUtils;

public class Playlist implements Parcelable {
    private String coverUrl;
    private String title;
    private int duration;
    private int songCount;
    private int id;
    private PartialArtist artist;
    private boolean isAlbum;

    private int[] trackIds;

    public Playlist(
            String coverUrl,
            String title,
            int duration,
            int songCount,
            int id,
            PartialArtist artist,
            int[] trackIds,
            boolean isAlbum
    ) {
        this.coverUrl = coverUrl;
        this.title = title;
        this.duration = duration;
        this.songCount = songCount;
        this.id = id;
        this.artist = artist;
        this.trackIds = trackIds;
        this.isAlbum = isAlbum;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public String getTitle() {
        return title;
    }

    public int getDuration() {
        return duration;
    }

    public String getFriendlyDuration() {
        return ApiUtils.getFriendlyDuration(duration);
    }

    public int getSongCount() {
        return songCount;
    }

    public int getId() {
        return id;
    }

    public PartialArtist getArtist() {
        return artist;
    }

    public boolean isAlbum() {
        return isAlbum;
    }

    public int[] getTrackIds() {
        return trackIds;
    }

    // Parcelable implementation
    protected Playlist(Parcel in) {
        coverUrl = in.readString();
        title = in.readString();
        duration = in.readInt();
        songCount = in.readInt();
        id = in.readInt();
        artist = in.readParcelable(PartialArtist.class.getClassLoader());
        trackIds = in.createIntArray();
        isAlbum = in.readInt() == 1 ? true : false;
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(coverUrl);
        parcel.writeString(title);
        parcel.writeInt(duration);
        parcel.writeInt(songCount);
        parcel.writeInt(id);
        parcel.writeParcelable(artist, i);
        parcel.writeIntArray(trackIds);
        parcel.writeInt(isAlbum ? 1 : 0);
    }
}
