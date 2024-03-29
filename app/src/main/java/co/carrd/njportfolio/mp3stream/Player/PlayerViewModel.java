package co.carrd.njportfolio.mp3stream.Player;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import co.carrd.njportfolio.mp3stream.SoundcloudApi.Models.Song;

public class PlayerViewModel extends ViewModel {
    private MutableLiveData<List<Song>> playerQueue = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<Song> currentSong = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> shuffleEnabled = new MutableLiveData<>(false);
    private MutableLiveData<Integer> repeatMode = new MutableLiveData<>(0);
    private MutableLiveData<Boolean> isLiked = new MutableLiveData<>(false);

    public MutableLiveData<Song> getCurrentSong() {
        return currentSong;
    }

    public MutableLiveData<Boolean> getIsPlaying() {
        return isPlaying;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<List<Song>> getPlayerQueue() {
        return playerQueue;
    }

    public void setCurrentSong(Song song) {
        currentSong.setValue(song);
    }

    public MutableLiveData<Boolean> getShuffleEnabled() {
        return shuffleEnabled;
    }

    public MutableLiveData<Integer> getRepeatMode() {
        return repeatMode;
    }

    public MutableLiveData<Boolean> getIsLiked() {
        return isLiked;
    }

    public void setSong(Song song, boolean isLiked) {
        getCurrentSong().setValue(song);
        getIsLiked().setValue(isLiked);
    }
}
