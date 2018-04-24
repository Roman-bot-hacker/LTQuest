package com.eliot.ltq.ltquest;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class SecondScreen extends AppCompatActivity {
    FirebaseDataManager manager = new FirebaseDataManager();

    public void setCategoriesText(){
        final TextView firstButtonText = findViewById(R.id.button1_text);
        final TextView secondButtonText = findViewById(R.id.button2_text);
        final TextView thirdButtonText = findViewById(R.id.button3_text);
        manager.categoriesNamesListRetriever(new FirebaseDataManager.DataRetrieveListener(){
            @Override
            public void onSuccess() {
                firstButtonText.setText(manager.getQuestCategoryList().get(0).getName());
                secondButtonText.setText(manager.getQuestCategoryList().get(1).getName());
                thirdButtonText.setText(manager.getQuestCategoryList().get(2).getName());
            }
        });
    }

    public void startButtonsOnClickListener() {
        View category1 = findViewById(R.id.button1);
        View category2 = findViewById(R.id.button2);
        View category3 = findViewById(R.id.button3);
        View seeAll = findViewById(R.id.see_all);
        category1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        category2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        category3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
