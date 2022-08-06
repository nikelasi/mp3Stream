package co.carrd.njportfolio.mp3stream.Library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import co.carrd.njportfolio.mp3stream.MainActivity;
import co.carrd.njportfolio.mp3stream.MainApplication;
import co.carrd.njportfolio.mp3stream.R;
import co.carrd.njportfolio.mp3stream.Search.Classes.EndlessRecyclerViewScrollListener;
import co.carrd.njportfolio.mp3stream.Search.Results.SearchResultsAdapter;
import co.carrd.njportfolio.mp3stream.SoundcloudApi.ApiWrapper;
import co.carrd.njportfolio.mp3stream.Utils.UiUtils;

public class LibraryFragment extends Fragment {
    private static LibraryFragment instance;
    private LibraryViewModel libraryViewModel;

    private RecyclerView libraryPlaylistsRecyclerView;
    private LibraryPlaylistsAdapter libraryPlaylistsAdapter;

    public static LibraryFragment getInstance() {
        if (instance == null) {
            instance = new LibraryFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_library, container, false);
        setUpStyles(fragmentView);

        // Link UI
        libraryPlaylistsRecyclerView = fragmentView.findViewById(R.id.library_fragment_playlists_recycler_view);

        libraryViewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Fetch data
        libraryViewModel.syncData();

        // Set up recycler view
        libraryPlaylistsAdapter = new LibraryPlaylistsAdapter(this);
        libraryPlaylistsRecyclerView.setAdapter(libraryPlaylistsAdapter);
        LinearLayoutManager recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        libraryPlaylistsRecyclerView.setLayoutManager(recyclerViewLayoutManager);

    }

    public LibraryViewModel getLibraryViewModel() {
        return libraryViewModel;
    }

    private void setUpStyles(View fragmentView) {
        UiUtils.setToolbarGradientTitle(fragmentView);
    }
}
