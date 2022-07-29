package co.carrd.njportfolio.mp3stream.SoundcloudApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.carrd.njportfolio.mp3stream.SoundcloudApi.Models.PartialArtist;
import co.carrd.njportfolio.mp3stream.SoundcloudApi.Models.Playlist;
import co.carrd.njportfolio.mp3stream.SoundcloudApi.Models.Song;
import co.carrd.njportfolio.mp3stream.SoundcloudApi.Models.SongCollection;

public class ApiParser {

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
                    obj.getInt("total_results"),
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

            String partialStreamUrl = songData.getJSONObject("media")
                    .getJSONArray("transcodings")
                    .getJSONObject(0)
                    .getString("url");

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
}
