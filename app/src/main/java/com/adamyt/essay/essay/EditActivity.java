package com.adamyt.essay.essay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
//        TextView textView = findViewById(R.id.textView);
//        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
//        textView.setText(message);
    }
}
