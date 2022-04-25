package edu.staybalanced.staybalanced;

import android.content.Context;
import android.media.MediaPlayer;

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
    public static void play(Context context, int audio) {
        // Load the audio file
        MediaPlayer mediaPlayer = MediaPlayer.create(context, audio);

        // Set a listener that releases the MediaPlayer from memory once the audio is done playing
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });

        // Play the audio
        mediaPlayer.start();
    }
}