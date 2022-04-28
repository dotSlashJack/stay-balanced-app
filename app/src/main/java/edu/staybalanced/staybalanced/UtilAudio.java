package edu.staybalanced.staybalanced;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import androidx.preference.PreferenceManager;

/**
 * A utility class that offers the ability to play audio resource files using Android's MediaPlayer
 *
 * Beware that, unlike UtilTTS, this class has no way of queueing audio up to play one after the
 * other.  If two calls are made right next to each other as shown in the demonstration in Home.java,
 * they will play over each other.  The timing of the audio clips is left up to the caller.
 *
 * https://developer.android.com/guide/topics/media/mediaplayer
 *
 * Audio Source: https://voicemaker.in/
 */
public class UtilAudio {

    // Constants
    public static final int
            IN_POSITION = R.raw.audio_default_position_in,
            OFF_POSITION = R.raw.audio_default_position_off,
            FIVE_LEFT = R.raw.audio_default_five_seconds,
            COUNTDOWN = R.raw.audio_default_321,
            DONE = R.raw.audio_default_done;


    // This method is used to play the audio files referenced by the constants defined above
    public static MediaPlayer playNow(Context context, MediaPlayer mediaPlayer, int audio) {
        /**
         *  Check if the user has turned on Silent Mode in Settings.java before playing audio
         *
         * If no "silent" preference is found (which should never happen as long as settings.xml is
         * coded correctly) then the default is to assume that Silent Mode is off (its value is false).
         */
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.getBoolean("silent", false)) { return mediaPlayer; }

        // Load the audio file
        MediaPlayer newFile = MediaPlayer.create(context, audio);

        // If the MediaPlayer is already playing, cancel playback
        mediaPlayer.release();

        // Play the audio
        newFile.start();

        return newFile;
    }

    public static MediaPlayer playLater(Context context, MediaPlayer mediaPlayer, int audio) {
        /**
         *  Check if the user has turned on Silent Mode in Settings.java before playing audio
         *
         * If no "silent" preference is found (which should never happen as long as settings.xml is
         * coded correctly) then the default is to assume that Silent Mode is off (its value is false).
         */
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.getBoolean("silent", false)) { return mediaPlayer; }

        // Load the next audio clip
        MediaPlayer nextFile = MediaPlayer.create(context, audio);

        // If the MediaPlayer is already playing, cancel playback if now == true
        // else enqueue the next audio file
        if (mediaPlayer.isPlaying()) {
            // Set a listener that releases the MediaPlayer from memory once the audio is done playing
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                }
            });
            mediaPlayer.setNextMediaPlayer(nextFile);
        } else {
            mediaPlayer.release();
            nextFile.start();
        }

        // Play the audio
        return nextFile;
    }
}