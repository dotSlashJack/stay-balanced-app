package edu.staybalanced.staybalanced;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * This utility encodes text into audio.  It is not recommended to be used since the encoding takes
 * more than a few milliseconds to initialize.
 *
 * It is better to "pre-render" the audio and store them as resource files to be played immediately.
 * This can be done with UtilAudio instead.
 *
 * Based on: https://www.youtube.com/watch?v=DoYnz0GYN1w
 */
public class UtilTTS {

    private TextToSpeech tts;

    public UtilTTS(Context context) {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // A TextToSpeech instance was successfully initialized on the device

                    // Attempt to set the instance's language it will decode and encode in
                    int result = tts.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // TODO: Degrade app functionality gracefully
                        Log.e(this.getClass().getName(), "Language not supported");
                    } else {
                        // TODO: Set flag that says tts is fully initialized
                    }
                } else {
                    // A TextToSpeech instance failed to initialize on the device
                    Log.e(this.getClass().getName(), "Initialization failed");
                }
            }
        });
    }

    // TODO: These say___() methods should check if tts is fully initialized before running
    public void sayNow(String msg) {
        // Stop whatever was playing before and play the given message instead
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void sayAfter(String msg) {
        // Add the given message to the queue and play it only after all prior messages are played
        tts.speak(msg, TextToSpeech.QUEUE_ADD, null, null);
    }

    public void destroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
