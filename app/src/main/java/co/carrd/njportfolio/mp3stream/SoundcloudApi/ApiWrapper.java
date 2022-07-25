package co.carrd.njportfolio.mp3stream.SoundcloudApi;

import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Implementation of a wrapper for Soundcloud API
 */
public class ApiWrapper {
  private static OkHttpClient client;
  private static String clientId;
  
  public ApiWrapper() {
    if (client == null) {
      client = new OkHttpClient();
      this.setClientId();
    }
  }

  /**
   * get search suggestions for a given query
   *
   * @param query query to get search suggestions for
   * @returns list containing search suggestions
   */
  public void getSearchSuggestions(String query, Consumer<List<String>> consumer) {
    // Construct endpoint url
    query = Uri.encode(query);
    String url = "https://api-v2.soundcloud.com/search/queries?q=" + query + "&client_id=" + clientId;

    // Fetch api response for search suggestions
    client.newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {
      @Override
      public void onFailure(@NonNull Call call, @NonNull IOException e) {
        System.out.println("ERROR: Failed to fetch search suggestions");
        e.printStackTrace();
      }

      @RequiresApi(api = Build.VERSION_CODES.N)
      @Override
      public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        try (ResponseBody responseBody = response.body()) {
          if (!response.isSuccessful()) throw new IOException("Unexpected Code: " + response);
          String bodyString = responseBody.string();

          // Parse response into array of search suggestion
          List<String> searchSuggestions = new ArrayList<>();
          JSONObject obj = new JSONObject(bodyString);
          JSONArray searchSuggestionsArray = obj.getJSONArray("collection");
          for (int i = 0; i < searchSuggestionsArray.length(); i++) {
            searchSuggestions.add((String) ((JSONObject) searchSuggestionsArray.get(i)).get("query"));
          }

          // Execute callback with requested data
          consumer.accept(searchSuggestions);


        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Get Client ID for Soundcloud API based on discovered pattern
   * 
   * @return Soundcloud API Client ID
   */
  private void setClientId() {
    // Fetch Soundcloud home page
    client.newCall(new Request.Builder().url("https://soundcloud.com").build()).enqueue(new Callback() {
      @Override
      public void onFailure(@NonNull Call call, @NonNull IOException e) {
        Log.e("API_WRAPPER", "No Connection!");
        e.printStackTrace();
      }

      @Override
      public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        try (ResponseBody responseBody = response.body()) {
          if (!response.isSuccessful()) throw new IOException("Unexpected Code: " + response);
          Log.d("API_WRAPPER", "Hi Again!");
          String bodyString = responseBody.string();

          // Get all the js file URLs found that contains the file URL with the Client ID
          String regex = "(https://a-v2\\.sndcdn\\.com/assets/)(.*?)(\\.js)";
          List<String> matches = new ArrayList<>();
          Matcher matcher = Pattern.compile(regex).matcher(bodyString);
          while (matcher.find()) {
            matches.add(matcher.group());
          }

          // file URL containing Client ID is the last match
          String clientIdFileUrl = matches.get(matches.size() - 1);

          // Fetch the file to find the Client ID
          client.newCall(new Request.Builder().url(clientIdFileUrl).build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
              e.printStackTrace();
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
              try (ResponseBody responseBody = response.body()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected Code: " + response);
                String bodyString = responseBody.string();

                // Match the values in any key-value pair with key being client_id
                String regex = "(client_id:\")(.*?)(\")";
                List<String> matches = new ArrayList<>();
                Matcher matcher = Pattern.compile(regex).matcher(bodyString);

                // The one that is most frequently matched is known to be the Client ID
                // Hence, determine most frequently matched id
                HashMap<String, Integer> occuranceCountMap = new HashMap<>();
                String mostCommonOccurance = "";
                int maxOccurances = 0;

                while (matcher.find()) {
                  String id = matcher.group(2);
                  Optional<Integer> occuranceCount = Optional.ofNullable(occuranceCountMap.get(id));
                  int newOccuranceCount = !occuranceCount.isPresent() ? 1 : occuranceCount.get() + 1;
                  occuranceCountMap.put(id, newOccuranceCount);

                  if (newOccuranceCount > maxOccurances) {
                    maxOccurances = newOccuranceCount;
                    mostCommonOccurance = id;
                  }
                }

                // Set Client ID to the most frequently matched
                clientId = mostCommonOccurance;
                Log.d("API_WRAPPER", clientId == ""
                        ? "ERROR: Client ID could not be fetched"
                        : "DEBUG: Client ID found: " + clientId);

              }
            }
          });
        }
      }
    });
  }
}