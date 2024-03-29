package co.carrd.njportfolio.mp3stream.SoundcloudApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.carrd.njportfolio.mp3stream.SoundcloudApi.Models.Artist;
import co.carrd.njportfolio.mp3stream.SoundcloudApi.Models.ArtistCollection;
import co.carrd.njportfolio.mp3stream.SoundcloudApi.Models.PartialArtist;
import co.carrd.njportfolio.mp3stream.SoundcloudApi.Models.Playlist;
import co.carrd.njportfolio.mp3stream.SoundcloudApi.Models.PlaylistCollection;
import co.carrd.njportfolio.mp3stream.SoundcloudApi.Models.Song;
import co.carrd.njportfolio.mp3stream.SoundcloudApi.Models.SongCollection;

public class ApiParser {

    public static ArtistCollection parseArtistCollection(String artistCollectionStringData) {
        try {
            JSONObject obj = new JSONObject(artistCollectionStringData);
            JSONArray artistsDataArray = obj.getJSONArray("collection");
            List<Artist> artistsArray = new ArrayList<>();
            for (int i = 0; i < artistsDataArray.length(); i++) {
                JSONObject artistData = (JSONObject) artistsDataArray.get(i);
                artistsArray.add(ApiParser.parseArtist(artistData));
            }

            return new ArtistCollection(
                    artistsArray,
                    obj.has("total_results")
                            ? obj.getInt("total_results")
                            : 0,
                    obj.has("next_href")
                            ? obj.getString("next_href")
                            : null
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Artist parseArtist(JSONObject artistData) {
        try {
            String name = artistData.getString("username");
            int id = artistData.getInt("id");
            int songCount = artistData.getInt("track_count");
            int playlistCount = artistData.getInt("playlist_count");

            String avatarUrl = artistData.getString("avatar_url").replace("large", "t500x500");
            String bannerUrl = null;
            if (!artistData.getString("visuals").equals("null")) {
                JSONObject artistVisualsData = artistData.getJSONObject("visuals");
                JSONArray visualsDataArray = artistVisualsData.getJSONArray("visuals");
                JSONObject visualData = (JSONObject) visualsDataArray.get(0);
                bannerUrl = visualData.getString("visual_url");
            }

            return new Artist(id, name, bannerUrl, avatarUrl, songCount, playlistCount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PlaylistCollection parsePlaylistCollection(String playlistCollectionStringData) {
        try {
            JSONObject obj = new JSONObject(playlistCollectionStringData);
            JSONArray playlistsDataArray = obj.getJSONArray("collection");
            List<Playlist> playlistsArray = new ArrayList<>();
            for (int i = 0; i < playlistsDataArray.length(); i++) {
                JSONObject playlistData = (JSONObject) playlistsDataArray.get(i);
                playlistsArray.add(ApiParser.parsePlaylist(playlistData));
            }

            return new PlaylistCollection(
                    playlistsArray,
                    obj.has("total_results")
                            ? obj.getInt("total_results")
                            : 0,
                    obj.has("next_href")
                            ? obj.getString("next_href")
                            : null
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Playlist parsePlaylist(JSONObject playlistData) {
        try {
            String title = playlistData.getString("title");
            int duration = playlistData.getInt("duration");
            int id = playlistData.getInt("id");
            String coverUrl = playlistData.getString("artwork_url");
            int songCount = playlistData.getInt("track_count");
            boolean isAlbum = playlistData.getBoolean("is_album");
            JSONArray playlistSongsDataArray = playlistData.getJSONArray("tracks");

            // If playlist has no cover art
            if (coverUrl.equals("null")) {
                // Use cover art of first track
                if (playlistSongsDataArray.length() > 0) {
                    JSONObject firstSong = (JSONObject) playlistSongsDataArray.get(0);
                    if (firstSong.has("artwork_url")) {
                        coverUrl = firstSong.getString("artwork_url");
                    }
                }
            }

            // Get alternate resolution image
            coverUrl = coverUrl.replace("large", "t500x500");

            PartialArtist partialArtist = parsePartialArtist(playlistData.getJSONObject("user"));

            // Get track ids
            List<Integer> trackIdsList = new ArrayList<>();
            for (int i = 0; i < playlistSongsDataArray.length(); i++) {
                JSONObject track = playlistSongsDataArray.getJSONObject(i);
                trackIdsList.add(track.getInt("id"));
            }
            int[] trackIds = trackIdsList.stream().mapToInt(e -> (int) e).toArray();

            return new Playlist(coverUrl, title, duration, songCount, id, partialArtist, trackIds, isAlbum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Song> parseSongList(String songListDataString) {
        try {
            JSONArray array = new JSONArray(songListDataString);

            List<Song> songList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject songData = array.getJSONObject(i);
                songList.add(ApiParser.parseSong(songData));
            }

            return songList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SongCollection parseSongCollection(String songCollectionDataString) {
        try {
            JSONObject obj = new JSONObject(songCollectionDataString);
            JSONArray songsDataArray = obj.getJSONArray("collection");
            List<Song> songsArray = new ArrayList<>();
            for (int i = 0; i < songsDataArray.length(); i++) {
                JSONObject songData = (JSONObject) songsDataArray.get(i);
                songsArray.add(ApiParser.parseSong(songData));
            }

            return new SongCollection(
                    songsArray,
                    obj.has("total_results")
                            ? obj.getInt("total_results")
                            : 0,
                    obj.has("next_href")
                            ? obj.getString("next_href")
                            : null
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Song parseSong(JSONObject songData) {
        try {
            String title = songData.getString("title");
            int duration = songData.getInt("duration");
            int id = songData.getInt("id");
            String coverUrl = songData.getString("artwork_url").replace("large", "t500x500");

            String partialStreamUrl = null;
            JSONArray transcodings = songData.getJSONObject("media").getJSONArray("transcodings");
            if (transcodings.length() > 0) {
                partialStreamUrl = transcodings.getJSONObject(0).getString("url");
            }

            String policy = songData.getString("policy");
            if (policy.equals("SNIP")) partialStreamUrl = null;

            PartialArtist partialArtist = parsePartialArtist(songData.getJSONObject("user"));

            return new Song(coverUrl, partialStreamUrl, title, duration, id, partialArtist);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PartialArtist parsePartialArtist(JSONObject artistData) {
        try {
            String artistName = artistData.getString("username");
            int artistId = artistData.getInt("id");
            String artistAvatarUrl = artistData.getString("avatar_url");
            return new PartialArtist(artistId, artistName, artistAvatarUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parseStreamUrl(String streamUrlStringData) {
        try {
            JSONObject obj = new JSONObject(streamUrlStringData);
            String streamUrl = obj.getString("url");
            return streamUrl;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> parseSearchSuggestions(String searchSuggestionsStringData) {
        try {
            List<String> searchSuggestions = new ArrayList<>();
            JSONObject obj = new JSONObject(searchSuggestionsStringData);
            JSONArray searchSuggestionsArray = obj.getJSONArray("collection");
            for (int i = 0; i < searchSuggestionsArray.length(); i++) {
                searchSuggestions.add((String) ((JSONObject) searchSuggestionsArray.get(i)).get("query"));
            }
            return searchSuggestions;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
