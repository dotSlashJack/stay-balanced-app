package edu.staybalanced.staybalanced;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Home extends Fragment {

    NavController navController;
    View.OnClickListener homeButtonListener;
    // TextToSpeech demonstration variable
    //UtilTTS tts;
    // MediaPlayer demonstration variable.  This should always
    MediaPlayer mediaPlayer = new MediaPlayer();

    public Home() {
        // Required empty public constructor
        super(R.layout.fragment_home);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO merge unlocked assets class to populate award place holder
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        homeButtonListener = view1 -> {
            switch (view1.getId()) {
                case R.id.home_btnStartExercise:
                    navController.navigate(NavGraph00Directions.toDoExercise()); //TODO: make sure this goes to the righth activity
                    break;
                case R.id.home_btnAddExercise:
                    navController.navigate(NavGraph00Directions.toNewExercise());
            }
        };
        view.findViewById(R.id.home_btnStartExercise).setOnClickListener(homeButtonListener);
        view.findViewById(R.id.home_btnAddExercise).setOnClickListener(homeButtonListener);

        // TextToSpeech demonstration variable initialization
        //tts = new UtilTTS(view.getContext());

        // TextToSpeech and MediaPlayer demonstration: set a click listener for the top half of Home
        ConstraintLayout top = view.findViewById(R.id.home_top);
        top.setOnClickListener(topHalf -> {
            /* TextToSpeech demonstration
             * The first time a message is sent to tts, it takes a while to process and play it
            tts.sayNow("Sample TTS Message");
            tts.sayAfter("3");
            tts.sayAfter("2");
            tts.sayAfter("1");
            tts.sayAfter("Rep done");
             */

            // MediaPlayer demonstration
            mediaPlayer = UtilAudio.playNow(view.getContext(), mediaPlayer, UtilAudio.COUNTDOWN);
            mediaPlayer = UtilAudio.playLater(view.getContext(), mediaPlayer, UtilAudio.DONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Set a listener that releases the MediaPlayer from memory once the audio is done playing
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });
    }
}