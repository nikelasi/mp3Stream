package co.carrd.njportfolio.mp3stream.Equalizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.audiofx.Equalizer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import co.carrd.njportfolio.mp3stream.MainApplication;

public class EqualizerViewModel extends ViewModel {
    private SharedPreferences sharedPreferences = MainApplication.getInstance().getSharedPreferences("equalizer", Context.MODE_PRIVATE);
    private MutableLiveData<int[]> bandLevels = new MutableLiveData<>(new int[5]);
    private MutableLiveData<Integer> selectedPresetIndex = new MutableLiveData<>(0);
    private MutableLiveData<Boolean> enabled = new MutableLiveData<>(false);

    public MutableLiveData<int[]> getBandLevels() {
        return bandLevels;
    }
    public MutableLiveData<Integer> getSelectedPresetIndex() {
        return selectedPresetIndex;
    }
    public MutableLiveData<Boolean> getEnabled() {
        return enabled;
    }

    public void syncEqualizer(Equalizer equalizer) {
        selectedPresetIndex.setValue(sharedPreferences.getInt("selectedPresetIndex", 0));
        enabled.setValue(sharedPreferences.getBoolean("enabled", false));
        equalizer.setEnabled(enabled.getValue());
        int bands = equalizer.getNumberOfBands();
        int[] storedBandLevels = new int[bands];
        for (int i = 0; i < bands; i++) {
            int bandLevel = sharedPreferences.getInt("band" + i, equalizer.getCenterFreq((short) i));
            equalizer.setBandLevel((short) i, (short) bandLevel);
            storedBandLevels[i] = bandLevel;
        }
        bandLevels.setValue(storedBandLevels);
    }

    public void updateEqualizer(Equalizer equalizer) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedPresetIndex", selectedPresetIndex.getValue());
        editor.putBoolean("enabled", enabled.getValue());
        for (int i = 0; i < bandLevels.getValue().length; i++) {
            editor.putInt("band" + i, bandLevels.getValue()[i]);
        }
        editor.commit();
    }

    public void usePreset(Equalizer equalizer, short presetNumber) {
        equalizer.usePreset(presetNumber);
        syncBandLevels(equalizer);
    }

    public void syncBandLevels(Equalizer equalizer) {
        int[] syncedBandLevels = new int[equalizer.getNumberOfBands()];
        for (int i = 0; i < equalizer.getNumberOfBands(); i++) {
            syncedBandLevels[i] = equalizer.getBandLevel((short) i);
        }
        bandLevels.setValue(syncedBandLevels);
        updateEqualizer(equalizer);
    }

    public void updateBandLevel(short newBandLevel, short bandNum, Equalizer equalizer) {
        int[] modifiedBandLevels = bandLevels.getValue();
        modifiedBandLevels[bandNum] = newBandLevel;
        equalizer.setBandLevel(bandNum, newBandLevel);
        bandLevels.setValue(modifiedBandLevels);
        updateEqualizer(equalizer);
    }


    public void toggleEnabled(Equalizer equalizer) {
        enabled.setValue(!enabled.getValue());
        equalizer.setEnabled(enabled.getValue());
        updateEqualizer(equalizer);
    }
}
