package edu.staybalanced.staybalanced;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SaveSettingsTesting extends AppCompatActivity {

    private Switch vibrate;
    private Button pressMe, resetPress;
    private TextView visitHistoryTextView;

    public static final int CLICK_PRESSME = 1;
    public static final int CLICK_VIBRATE = 2;
    public static final int CLICK_RESET = 3;

    // Preferred alternative to enums
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CLICK_PRESSME, CLICK_VIBRATE, CLICK_RESET})
    public @interface CLICKED{}
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PRESS_COUNT = "count";
    public static final String VIBRATE_SETTING = "vibration";

    // Final state
    private String prefsText;
    private Boolean vibrateOnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_settings_testing);

        vibrate = findViewById(R.id.vibrate);
        pressMe = findViewById(R.id.pressMe);
        resetPress = findViewById(R.id.resetPress);
        visitHistoryTextView = findViewById(R.id.visitHistoryTextView);

        pressMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePrefs(CLICK_PRESSME);

                loadPrefs();
                updatePrefsViews();
            }
        });

        vibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePrefs(CLICK_VIBRATE);

                loadPrefs();
                updatePrefsViews();
            }
        });

        resetPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePrefs(CLICK_RESET);

                loadPrefs();
                updatePrefsViews();
            }
        });

        loadPrefs();
        updatePrefsViews();
    }


    private void savePrefs(@CLICKED int value) {
    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();

    switch(value) {
        case(CLICK_PRESSME):
            editor.putInt(PRESS_COUNT, sharedPreferences.getInt(PRESS_COUNT, 0) + 1);
            break;
        case(CLICK_VIBRATE):
            editor.putBoolean(VIBRATE_SETTING, vibrate.isChecked());
            break;
        case(CLICK_RESET):
            editor.putInt(PRESS_COUNT, 0);
            editor.putBoolean(VIBRATE_SETTING, true);
            break;
        default:
            break;
    }
    editor.apply();
    }

    private void loadPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        vibrateOnOff = sharedPreferences.getBoolean(VIBRATE_SETTING, true);
        prefsText = "The button click count is: " + sharedPreferences.getInt(PRESS_COUNT, 0);
    }

    private void updatePrefsViews() {
        visitHistoryTextView.setText(prefsText);
        vibrate.setChecked(vibrateOnOff);
    }
}


