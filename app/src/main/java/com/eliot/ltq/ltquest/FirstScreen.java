package com.eliot.ltq.ltquest;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class FirstScreen extends AppCompatActivity{
    private final View firstScreenView = findViewById(R.id.screen1);
    private final View secondScreenViev = findViewById(R.id.screen2);

    public void startButtonsOnClickListener() {
        Button startNew = findViewById(R.id.start_new);
        Button continueQuest = findViewById(R.id.continue_quest);
        startNew.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstScreenView.setVisibility(View.GONE);
                secondScreenViev.setVisibility(View.VISIBLE);
            }
        });
        continueQuest.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
