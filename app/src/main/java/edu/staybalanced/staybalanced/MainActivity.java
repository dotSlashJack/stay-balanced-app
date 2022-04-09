package edu.staybalanced.staybalanced;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button goToRecyclerViewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        goToRecyclerViewButton = findViewById(R.id.IDbottomBarButton);
        goToRecyclerViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRecyclerView(view);
            }
        });
    }

    public void showJack(View view) {
        Intent jack_intent = new Intent(MainActivity.this, JackTesting.class);
        startActivity(jack_intent);
    }

    public void goToRecyclerView(View v) {
        Intent goToRV = new Intent(MainActivity.this, Recycler_view_searchable_activity.class);
        startActivity(goToRV);
    }
}