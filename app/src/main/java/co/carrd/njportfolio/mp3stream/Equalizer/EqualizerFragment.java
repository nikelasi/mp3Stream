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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import co.carrd.njportfolio.mp3stream.MainActivity;
import co.carrd.njportfolio.mp3stream.MainApplication;
import co.carrd.njportfolio.mp3stream.R;
import co.carrd.njportfolio.mp3stream.Utils.UiUtils;

public class EqualizerFragment extends Fragment {
    private RecyclerView recyclerView;
    private Equalizer equalizer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_equalizer, container, false);
        setUpStyles(fragmentView);

        // Link UI
        recyclerView = fragmentView.findViewById(R.id.equalizer_bands_recycler_view);

        AutoCompleteTextView presetTextView = fragmentView.findViewById(R.id.preset_text_field);
        equalizer = new Equalizer(0, MainApplication.getInstance().getPlayer().getAudioSessionId());
        String[] presets = new String[equalizer.getNumberOfPresets()];
        for (short i = 0; i < presets.length; i++) {
            presets[i] = equalizer.getPresetName(i);
        }
        presetTextView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, presets));
        presetTextView.setText(presets[0], false);
//        presetTextView.setOnItemClickListener((adapterView, view, position, id) -> {
//            // Select Preset
//            equalizer.usePreset((short) position);
//        });



//        equalizer.usePreset();

        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Set up equalizer band recycler view
        recyclerView.setAdapter(new EqualizerBandsAdapter(equalizer));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int totalWidth = parent.getMeasuredWidth();
                int itemCount = state.getItemCount();
                int spacing = (totalWidth - convertToPixels(32) * itemCount) / (itemCount + 1);
                Log.d("EQUALIZER", totalWidth + " " + spacing + " " + view.getMeasuredWidth() + " " + view.getPaddingLeft());
//                if (parent.getChildLayoutPosition(view) != itemCount - 1) {
                outRect.left = spacing;
//                }
            }
        });
    }

    private void setUpStyles(View fragmentView) {
        UiUtils.setToolbarGradientTitle(fragmentView);
    }

    private int convertToPixels(int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }
}
