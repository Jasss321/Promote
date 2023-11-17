package com.example.promote;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TeamActivity extends AppCompatActivity {

    private Button backButton;

    // This method is called when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting the layout for the activity using the activity_team.xml file
        setContentView(R.layout.activity_team);

        // Finds the back button in the activity_team.xml file
        backButton = findViewById(R.id.back_button);

        // Set an OnClickListener on the back button to finish the activity when it's clicked
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
