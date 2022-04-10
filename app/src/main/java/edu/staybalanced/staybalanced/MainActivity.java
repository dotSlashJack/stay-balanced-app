package edu.staybalanced.staybalanced;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String SHARED_PREFS = "sharedPrefs";
    private Button settingsPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingsPrefs = (Button) findViewById(R.id.settingPrefsBtn);
        settingsPrefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent saveSettings = new Intent(MainActivity.this, SaveSettingsTesting.class);
                startActivity(saveSettings);
            }
        });

        // SharedPrefs Demo
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String clicked = "The button in SaveSettings has a click count of: "
                + sharedPreferences.getInt("count", 0);
        TextView tv_savedPrefs = findViewById(R.id.tv_savedPrefs);
        tv_savedPrefs.setText(clicked);
    }

    public void showJack(View view) {
        Intent jack_intent = new Intent(MainActivity.this, JackTesting.class);
        startActivity(jack_intent);
    }
}