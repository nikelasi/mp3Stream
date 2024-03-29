package co.carrd.njportfolio.mp3stream.SoundcloudApi.Models;

import co.carrd.njportfolio.mp3stream.SoundcloudApi.ApiUtils;

public class Song {
  private String coverUrl;
  private String partialStreamUrl;
  private String title;
  private int duration;
  private int id;
  private PartialArtist artist;

  public Song(String coverUrl, String partialStreamUrl, String title, int duration, int id, PartialArtist artist) {
    this.coverUrl = coverUrl;
    this.partialStreamUrl = partialStreamUrl;
    this.title = title;
    this.duration = duration;
    this.id = id;
    this.artist = artist;
  }

  public String getCoverUrl() {
    return coverUrl;
  }

  public String getPartialStreamUrl() {
    return partialStreamUrl;
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

  public int getId() {
    return id;
  }

  public PartialArtist getArtist() {
    return artist;
  }
  
}
