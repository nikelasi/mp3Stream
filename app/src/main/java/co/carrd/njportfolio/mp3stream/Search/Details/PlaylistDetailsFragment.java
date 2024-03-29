package co.carrd.njportfolio.mp3stream.Search.Details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.carrd.njportfolio.mp3stream.MainApplication;
import co.carrd.njportfolio.mp3stream.Player.PlayerFragment;
import co.carrd.njportfolio.mp3stream.R;
import co.carrd.njportfolio.mp3stream.Search.Classes.EndlessRecyclerViewScrollListener;
import co.carrd.njportfolio.mp3stream.Search.Results.SearchResultsAdapter;
import co.carrd.njportfolio.mp3stream.Search.SearchFragment;
import co.carrd.njportfolio.mp3stream.SoundcloudApi.ApiUtils;
import co.carrd.njportfolio.mp3stream.SoundcloudApi.ApiWrapper;
import co.carrd.njportfolio.mp3stream.SoundcloudApi.Models.Playlist;
import co.carrd.njportfolio.mp3stream.SoundcloudApi.Models.Song;
import co.carrd.njportfolio.mp3stream.Utils.UiUtils;

public class PlaylistDetailsFragment extends Fragment {
    private Playlist playlist;

    private ImageButton navBackButton;
    private ImageView coverImageView;
    private TextView playlistNameTextView;
    private TextView songCountTextView;
    private TextView durationTextView;
    private TextView topbarTitleTextView;
    private RecyclerView songsRecyclerView;
    private SearchResultsAdapter songsRecyclerViewAdapter;
    private ProgressBar progressBar;
    private ImageButton playButton;
    private ConstraintLayout noSongsView;

    private int songsPage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_playlist_details, container, false);

        // Retrieve data
        Bundle args = getArguments();
        playlist = args.getParcelable("playlist");

        // Link UI
        navBackButton = fragmentView.findViewById(R.id.nav_back_button);
        coverImageView = fragmentView.findViewById(R.id.playlist_details_cover);
        playlistNameTextView = fragmentView.findViewById(R.id.playlist_details_name_text_view);
        songCountTextView = fragmentView.findViewById(R.id.playlist_details_song_count_text_view);
        durationTextView = fragmentView.findViewById(R.id.playlist_details_duration_text_view);
        topbarTitleTextView = fragmentView.findViewById(R.id.playlist_details_topbar_title);
        songsRecyclerView = fragmentView.findViewById(R.id.playlist_details_songs_recycler_view);
        progressBar = fragmentView.findViewById(R.id.playlist_details_endless_progress);
        playButton = fragmentView.findViewById(R.id.playlist_details_play_button);
        noSongsView = fragmentView.findViewById(R.id.view_no_songs);

        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up back nav
        navBackButton.setOnClickListener(v -> SearchFragment.getInstance().popBackStack());

        // Set up play button
        playButton.setOnClickListener(v -> playPlaylist(0));

        // Load cover image
        UiUtils.loadImage(coverImageView, playlist.getCoverUrl());

        // Load playlist metadata
        playlistNameTextView.setText(playlist.getTitle());
        songCountTextView.setText(playlist.getSongCount() + " Songs");
        durationTextView.setText(playlist.getFriendlyDuration());
        topbarTitleTextView.setText(playlist.isAlbum() ? "Album" : "Playlist");

        // Set up recycler view
        songsRecyclerViewAdapter = new SearchResultsAdapter(this);
        songsRecyclerView.setAdapter(songsRecyclerViewAdapter);
        LinearLayoutManager recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        songsRecyclerView.setLayoutManager(recyclerViewLayoutManager);
        songsRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(recyclerViewLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                int[] paginatedTrackIds = ApiUtils.paginateIds(songsPage, playlist.getTrackIds());
                if (paginatedTrackIds != null) {
                    songsPage++;
                    progressBar.setVisibility(View.VISIBLE);
                    ApiWrapper soundcloudApi = MainApplication.getInstance().getSoundcloudApi();
                    soundcloudApi.getPlaylistTracks(paginatedTrackIds, playlistTracks -> {
                        List<? extends Object> castedResults = playlistTracks;
                        UiUtils.runOnUiThread(getActivity(), () -> {
                            songsRecyclerViewAdapter.getSearchResults().getValue().addAll((List<Object>) castedResults);
                            songsRecyclerViewAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.INVISIBLE);
                        });
                    });
                }
            }
        });

        getInitialSongs();
    }

    public void getInitialSongs() {
        songsPage = 1;
        int[] paginatedTrackIds = ApiUtils.paginateIds(songsPage, playlist.getTrackIds());

        noSongsView.setVisibility(View.GONE);
        if (paginatedTrackIds == null) {
            noSongsView.setVisibility(View.VISIBLE);
            return;
        }
        songsPage++;

        ApiWrapper soundcloudApi = MainApplication.getInstance().getSoundcloudApi();
        soundcloudApi.getPlaylistTracks(paginatedTrackIds, playlistTracks -> {
            List<? extends Object> castedResults = playlistTracks;
            UiUtils.runOnUiThread(getActivity(), () -> {
                songsRecyclerViewAdapter.getSearchResults().setValue((List<Object>) castedResults);
                songsRecyclerViewAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);
            });
        });
    }

    public void playPlaylist(int startSongIndex) {
        if (playlist.getTrackIds().length == 0) return;
        List<int[]> chunkedIds = new ArrayList<>();
        int chunkSize = 40;
        int[] trackIds = playlist.getTrackIds();
        int chunk = 0;
        while ((chunk * chunkSize) < trackIds.length) {
            chunkedIds.add(ApiUtils.paginateIds(chunk + 1, trackIds, chunkSize));
            chunk++;
        }
        fetchSongs(chunkedIds, new ArrayList<>(), 0, startSongIndex);
    }

    private void fetchSongs(List<int[]> chunkedIds, List<Song> aggregatedSongsList, int chunk, int startSongIndex) {
        MainApplication.getInstance().getSoundcloudApi().getPlaylistTracks(chunkedIds.get(chunk), songsList -> {
            UiUtils.runOnUiThread(getActivity(), () -> {
                aggregatedSongsList.addAll(songsList);
                if (chunk < chunkedIds.size()) {
                    // Last already
                    PlayerFragment.getInstance().setPlaylist(aggregatedSongsList, startSongIndex);
                } else {
                    // Continue
                    fetchSongs(chunkedIds, aggregatedSongsList, chunk + 1, startSongIndex);
                }
            });
        });
    }
}
