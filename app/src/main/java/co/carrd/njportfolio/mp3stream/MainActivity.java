package co.carrd.njportfolio.mp3stream;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import co.carrd.njportfolio.mp3stream.Equalizer.EqualizerFragment;
import co.carrd.njportfolio.mp3stream.Equalizer.EqualizerViewModel;
import co.carrd.njportfolio.mp3stream.Library.LibraryFragment;
import co.carrd.njportfolio.mp3stream.Library.LibraryViewModel;
import co.carrd.njportfolio.mp3stream.Player.PlayerFragment;
import co.carrd.njportfolio.mp3stream.Search.SearchFragment;

public class MainActivity extends AppCompatActivity {

    private Fragment libraryFragment = LibraryFragment.getInstance();
    private Fragment searchFragment = SearchFragment.getInstance();
    private Fragment equalizerFragment = new EqualizerFragment();

    private Fragment playerFragment = PlayerFragment.getInstance();

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link UI
        bottomNav = findViewById(R.id.bottom_navigation);

        // Set up bottom nav
        bottomNav.setOnItemSelectedListener(navListener);
        bottomNav.setItemIconTintList(null); // This line is needed to have gradient icons work

        // Open search fragment as first fragment
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.main_fragment_container, searchFragment).commit();
        bottomNav.setSelectedItemId(R.id.bottom_nav_search);

        // Insert player fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.player_fragment_container, playerFragment)
                .commitNow();

        // Ensure equalizer settings are restored on start
        new ViewModelProvider(this).get(EqualizerViewModel.class).syncEqualizer(MainApplication.getInstance().getEqualizer());

        // Ensure library view model are restored
        new ViewModelProvider(this).get(LibraryViewModel.class).syncData();
    }

    private NavigationBarView.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.saveBackStack("LIBRARY_FRAGMENT");
        fragmentManager.saveBackStack("SEARCH_FRAGMENT");

        switch (item.getItemId()) {
            case R.id.bottom_nav_library:
                selectedFragment = libraryFragment;
                break;
            case R.id.bottom_nav_search:
                selectedFragment = searchFragment;
                break;
            case R.id.bottom_nav_equalizer:
                selectedFragment = equalizerFragment;
                break;
        }

        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.main_fragment_container, selectedFragment).commit();

        try {
            if (selectedFragment.equals(searchFragment)) {
                fragmentManager.restoreBackStack("SEARCH_FRAGMENT");
            } else if (selectedFragment.equals(libraryFragment)) {
                fragmentManager.restoreBackStack("LIBRARY_FRAGMENT");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    };

    public BottomNavigationView getBottomNav() {
        return bottomNav;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            super.onBackPressed();
        } else {
            this.moveTaskToBack(true);
        }
    }
}