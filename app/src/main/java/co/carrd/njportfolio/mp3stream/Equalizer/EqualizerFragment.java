package co.carrd.njportfolio.mp3stream.Equalizer;

import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.Arrays;

import co.carrd.njportfolio.mp3stream.MainActivity;
import co.carrd.njportfolio.mp3stream.MainApplication;
import co.carrd.njportfolio.mp3stream.Player.PlayerViewModel;
import co.carrd.njportfolio.mp3stream.R;
import co.carrd.njportfolio.mp3stream.Utils.UiUtils;

public class EqualizerFragment extends Fragment {
    private RecyclerView recyclerView;
    private Equalizer equalizer;
    private EqualizerViewModel equalizerViewModel;

    private AutoCompleteTextView presetDropdown;
    private MaterialSwitch enableSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_equalizer, container, false);
        UiUtils.setToolbarGradientTitle(fragmentView);

        // Link UI
        recyclerView = fragmentView.findViewById(R.id.equalizer_bands_recycler_view);
        enableSwitch = fragmentView.findViewById(R.id.equalizer_enabled_switch);
        presetDropdown = fragmentView.findViewById(R.id.preset_text_field);

        // Link View Model
        equalizerViewModel = new ViewModelProvider(requireActivity()).get(EqualizerViewModel.class);

        // Initialise Equalizer
        equalizer = MainApplication.getInstance().getEqualizer();

        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Fetch all presets
        String[] presets = new String[equalizer.getNumberOfPresets()];
        for (short i = 0; i < presets.length; i++) {
            presets[i] = equalizer.getPresetName(i);
        }
        // Configure preset dropdown menu with fetched presets
        presetDropdown.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, presets));
        // Set preset to be Normal (preset number 0) by default
        equalizerViewModel.usePreset(equalizer, (short) 0);
        presetDropdown.setText(presets[0], false);
        // Configure preset to be set when selected from preset dropdown menu
        presetDropdown.setOnItemClickListener((adapterView, itemView, position, id) -> {
            equalizerViewModel.usePreset(equalizer, (short) position);
        });

        // Set up equalizer band recycler view
        recyclerView.setAdapter(new EqualizerBandsAdapter(equalizer, getActivity()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        // Add Item Decoration that is configured to give equal spacing
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);

                // Set up dimensions
                int totalWidth = parent.getMeasuredWidth();
                int itemCount = state.getItemCount();
                int equalizerBandWidth = convertToPixels(32);

                // Calculate dimensions
                int totalSpacing = (totalWidth) - (equalizerBandWidth * itemCount);
                int spacing = totalSpacing / (itemCount + 1); // itemCount + 1 -> number of gaps

                // Set offset
                outRect.left = spacing;
            }
        });

        // Configure equalizer to update band levels if changed from outside applications
        equalizer.setParameterListener((equalizer, status, paramType, bandNum, value) -> {
            if (paramType == Equalizer.PARAM_BAND_LEVEL) {
                equalizerViewModel.updateBandLevel((short) value, (short) bandNum, equalizer);
            }
        });

        // Configure enable switch to toggle whether equalizer is active
        enableSwitch.setOnClickListener(v -> equalizer.setEnabled(!equalizer.getEnabled()));
    }

    private int convertToPixels(int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }
}
